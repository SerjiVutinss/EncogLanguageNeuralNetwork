package ie.gmit.sw.nn;

import ie.gmit.sw.Utilities;
import org.encog.engine.network.activation.ActivationElliottSymmetric;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import java.io.FileNotFoundException;

public class LanguageNeuralNetwork {

    private BasicNetwork _network;
    private MLDataSet _trainingDataSet;
    private Propagation _propagation;
    private MLTrain _train;

    private double _maxSeconds = 180D;
    private double _minErrorRate = 0.00038;
    private int _maxEpochs = 17;

    private boolean isTrained;
    private NetworkData _networkData;

    public boolean getIsTrained() {
        return isTrained;
    }

    public LanguageNeuralNetwork(MLDataSet trainingDataSet) {
        System.out.println("\nBuilding new Neural Network");
        _trainingDataSet = trainingDataSet;
        build();
        createTrainer();
    }

    public LanguageNeuralNetwork(BasicNetwork existingNetwork, MLDataSet trainingDataSet) {
        this._trainingDataSet = trainingDataSet;
        this._network = existingNetwork;
        build();
        createTrainer();
        isTrained = true;
    }

    private void build() {

        // Store these for easier access
        int numInputs = _trainingDataSet.getInputSize();
        int numOutputs = _trainingDataSet.getIdealSize();

        // Calculate the ideal number of hidden nodes as per the Geometric Pyramid Rule.
        // This is number is useful as a starting point and provides a base value which scales
        // with input and output size.
        int geometricPyramidRuleSize = (int) Math.sqrt(numInputs * numOutputs);

        System.out.println("\tCreating Basic Network");
        _network = new BasicNetwork();

        // Build the topology

        // Add a dumb input layer - no activation or bias required
        System.out.println(buildLayerDescriptor("Input", numInputs, "None", false, -1));
        _network.addLayer(new BasicLayer(null, false, numInputs));

        int l1_count = (int) (geometricPyramidRuleSize * 0.22);
        int dropoutNeurons = (int)(geometricPyramidRuleSize * 0.25);

        System.out.println(buildLayerDescriptor("Hidden", l1_count, "ElliottSymmetric", true, -1));
        _network.addLayer(new BasicLayer(new ActivationElliottSymmetric(), true, l1_count));

        // SoftMax output layer - output layer should have values between 0 and 1
        System.out.println(buildLayerDescriptor("Output", numOutputs, "SoftMax", false, -1));
        _network.addLayer(new BasicLayer(new ActivationSoftMax(), false, numOutputs));

        _network.getStructure().finalizeStructure();
        _network.reset();
    }

    private void createTrainer() {
        System.out.println("\tCreating Folded Data Set...");
        final FoldedDataSet foldedDataSet = new FoldedDataSet(_trainingDataSet);

        System.out.println("\tCreating Resilient Propagation...");
        _propagation = new ResilientPropagation(_network, foldedDataSet);

        System.out.println("\tCreating Cross Validation Trainer...");
        _train = new CrossValidationKFold(_propagation, 5);
        System.out.println("\tFinished!");
    }

    public int train() {

        System.out.println("Training Network...");
        int _vectorPrecision = 3;


        // seconds
        double totalTime = 0;
        double epochDuration = 0;

        // nanoseconds
        long startTime = 0;
        long endTime = 0;

        int epoch = 1; //Use this to track the number of epochs
        double error;
        do {
            startTime = System.nanoTime();

            _train.iteration();
            endTime = System.nanoTime();

            error = _train.getError();

            epochDuration = ((double) (endTime - startTime) / 1000000000);
            totalTime += epochDuration;
            EpochData epochData = new EpochData(epoch, error, epochDuration);
            System.out.println("\t" + epochData + "; Total Time: " + totalTime);
            _networkData.addEpochData(epochData);

            epoch++;
        } while (error > _minErrorRate && epoch < _maxEpochs + 1 && totalTime < _maxSeconds);


        System.out.println(String.format("Trained in %d epochs", epoch - 1));
        isTrained = true;
        System.out.println(String.format("Total Training Time: %fs", totalTime));
        System.out.println();
        return epoch - 1;
    }

    public int classify(double[] vector) {
        return _network.classify(new BasicMLData(vector));
    }

    public void validate() {
        validate(_trainingDataSet);
    }

    public double validate(MLDataSet dataset) {

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

        if (_networkData != null) {
            _networkData.setTestAccuracy(accuracy);
        }

        return accuracy;
    }

    public void saveToFile(String filename) {
        Utilities.saveNeuralNetwork(_network, filename);
    }

    // Provide a simple means for describing each layer as it is added.
    private String buildLayerDescriptor(String type, int neurons, String activation, boolean bias, double dropout) {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\t\tAdding %s layer:", type));
        sb.append(String.format("{ Neurons: %s, ", neurons));
        sb.append(String.format("Activation: %s, ", activation));
        sb.append(String.format("Bias: %s", bias ? "Yes" : "No"));
        if (dropout > 0) {
            sb.append(String.format(", Dropout: %s", dropout));
        }
        sb.append("}");
        return sb.toString();
    }

    // Only for testing
    public void setNGrams(int minNGrams, int maxNGrams) {
        _networkData = new NetworkData(_trainingDataSet.getInputSize(), 3, minNGrams, maxNGrams);
    }

    // Only for testing
    public void writeNetworkDataToCSV(String filename) throws FileNotFoundException {
        if (_networkData != null) _networkData.writeToCsv(filename);
    }

    public void set_maxSeconds(double _maxSeconds) {
        this._maxSeconds = _maxSeconds;
    }

    public void set_minErrorRate(double _minErrorRate) {
        this._minErrorRate = _minErrorRate;
    }

    public void set_maxEpochs(int _maxEpochs) {
        this._maxEpochs = _maxEpochs;
    }

}