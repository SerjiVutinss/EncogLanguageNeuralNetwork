package ie.gmit.sw.nn;

import ie.gmit.sw.*;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationElliottSymmetric;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;

import java.io.File;
import java.io.FileNotFoundException;

public class WiliNeuralNetwork {

    private final int _maxEpochs;
    private final int _maxNGram;
    private final int _minNGram;
    private String _dataFilePath;
    private boolean _inputHasHeaders;

    private int _numInputs;
    private int _numOutputs;

    private double _learnRate;
    private double _momentum;
    private double _maxErrorRate;

    private BasicNetwork _network;
    private MLTrain _trainer;

    private double cvError;
    private MLTrain foldTrain;

    public void setTestSet(MLDataSet _testSet) {
        this._testSet = _testSet;
    }

    private NetworkData _networkData;

    private DatasetType _trainingDataSetType;
    private MLDataSet _trainingSet;
    private MLDataSet _testSet;

    public WiliNeuralNetwork(String dataFilePath, int numInputs, int numOutputs, int minNGram, int maxNGram) {

        this._minNGram = minNGram;
        this._maxNGram = maxNGram;

        _dataFilePath = dataFilePath;
        _inputHasHeaders = false;

        _numInputs = numInputs;
        _numOutputs = numOutputs;

        _learnRate = 0.01;
        _momentum = 0.2;
        _maxErrorRate = 0.00001;
        _maxEpochs = 1000;
    }

    public void saveToFile() {
        Utilities.saveNeuralNetwork(_network, "my-nn.nn");
    }

    public void loadNetwork(String filename) {
        _network = Utilities.loadNeuralNetwork(filename);
    }

    public void testVector(double[] vector) {
        MLData mlData = new BasicMLData(vector);

        int result = _network.classify(mlData);
        var langs = LanguageService.getLanguages();
        System.out.print(langs[result]);
    }


    public void setTrainingDataSetType(DatasetType trainingDataSetType) {
        _trainingDataSetType = trainingDataSetType;
    }

    public void build() {

        System.out.println("Building Network...");
        _network = new BasicNetwork();

        // Build the topology
        int geometricPyramidRuleSize = (int) Math.sqrt(_numInputs * _numOutputs); // Geometric Pyramid Rule to calculate hidden layer nodes

        // Add a dumb input layer - no activation or bias required
        System.out.println(buildLayerDescriptor("Input", _numInputs, "None", false, -1));
        _network.addLayer(new BasicLayer(null, false, _numInputs));

        int l1_count = (int) (geometricPyramidRuleSize * 0.2);

        System.out.println(buildLayerDescriptor("Hidden", l1_count, "ElliottSymmetric", true, -1));
        _network.addLayer(new BasicLayer(new ActivationElliottSymmetric(), true, l1_count));


        // SoftMax output layer - ouptut layer should have values between 0 and 1
        System.out.println(buildLayerDescriptor("Output", _numOutputs, "SoftMax", false, -1));
        _network.addLayer(new BasicLayer(new ActivationSoftMax(), false, _numOutputs));

        _network.getStructure().finalizeStructure();
        _network.reset();

        System.out.println("\tLoading CSV data...");
        _trainingSet = loadDataSet(_trainingDataSetType);

        System.out.println("\tCreating Folded Data Set...");
        final FoldedDataSet foldedDataSet = new FoldedDataSet(_trainingSet);
        System.out.println("\tCreating Resilient Propagation...");
        foldTrain = new ResilientPropagation(_network, foldedDataSet);
        System.out.println("\tCreating Cross Validation Trainer...");
        _trainer = new CrossValidationKFold(foldTrain, 5);

        System.out.println("Network Built.");
    }


    public int train() {

        System.out.println("Training Network...");
        int _vectorPrecision = 3;
        _networkData = new NetworkData(_numInputs, _vectorPrecision, _minNGram, _maxNGram);

        // seconds
        double totalTime = 0;
        double duration = 0;

        // nanoseconds
        long startTime = 0;
        long endTime = 0;

        int epoch = 1; //Use this to track the number of epochs
        double error;
        do {
            startTime = System.nanoTime();

            _trainer.iteration();
            endTime = System.nanoTime();

            error = _trainer.getError();

            duration = ((double) (endTime - startTime) / 1000000000);
            totalTime += duration;
            EpochData epochData = new EpochData(epoch, error, duration);
            System.out.println("\t" + epochData + "; Total Time: " + totalTime);
            _networkData.addEpochData(epochData);

            epoch++;
        } while (error > _maxErrorRate && epoch < _maxEpochs + 1 && totalTime < 180d);


        System.out.println(String.format("Trained in %d epochs", epoch - 1));
        System.out.println(String.format("Total Training Time: %fs", totalTime));
        System.out.println();
        return epoch - 1;
    }

    public void writeNetworkDataToCSV(String filename) throws FileNotFoundException {
        if (_networkData != null) _networkData.writeToCsv(filename);
    }


    public void shutdown() {
        Encog.getInstance().shutdown();
    }

    public void printTestResults(boolean isTestData) {

        MLDataSet testDataSet;

        if (!isTestData) {
            testDataSet = _trainingSet;
        } else {
            DatasetType testSetType;
            boolean isSmallDataSet = _trainingDataSetType == DatasetType.LARGE;
            if (isSmallDataSet) {
                testDataSet = loadDataSet(DatasetType.SMALL);
            } else {
                testDataSet = loadDataSet(DatasetType.LARGE);
            }
        }

        double accuracy = validate(testDataSet);

        if (_networkData != null) {
            if (isTestData) _networkData.setTrainingAccuracy(accuracy);
            else _networkData.setTestAccuracy(accuracy);
        }
    }

    private double validate(MLDataSet dataset) {

        int count = 0;
        for (int i = 0; i < dataset.size(); i++) {

            final MLDataPair pair = dataset.get(i);
//            MLData output = _network.compute(pair.getInput());

            int expected = Utilities.deserialiseCategories(pair.getIdeal().getData());

            int actual = _network.classify(pair.getInput());

            if (expected == actual) count++;
        }

        double accuracy = (double) count / dataset.size();

        System.out.println(String.format("Results Correct: %d/%d", count, dataset.size()));
        System.out.println(String.format("Accuracy %f%%", accuracy * 100));

        return accuracy;
    }


    private MLDataSet loadDataSet(DatasetType datasetType) {

        String filename = VectorFileCreator.buildFilename(_numInputs, datasetType, _minNGram, _maxNGram);

        DataSetCODEC dsc = new CSVDataCODEC(new File(filename), CSVFormat.ENGLISH, _inputHasHeaders, _numInputs, _numOutputs, false);
        MemoryDataLoader mdl = new MemoryDataLoader(dsc);

        return mdl.external2Memory();
    }

//    private EpochData buildEpochData(int epoch, double error, double duration) {
//
//        if (_networkData == null) {
//            _networkData = new NetworkData(_numInputs);
//        }
//        var epochData = new EpochData(epoch, error, duration);
//        _networkData.addEpochData(epochData);
//
//        return epochData;
//    }

    private String buildLayerDescriptor(String type, int neurons, String activation, boolean bias, double dropout) {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\tCreating %s layer:", type));
        sb.append(String.format("\n\t\tNeurons: %s", neurons));
        sb.append(String.format("\n\t\tActivation: %s", activation));
        sb.append(String.format("\n\t\tBias: %s", bias ? "Yes" : "No"));
        if (dropout > 0) {
            sb.append(String.format("\n\t\tDropout: %s", dropout));
        }
        return sb.toString();
    }

}
