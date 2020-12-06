package ie.gmit.sw.ui;

import ie.gmit.sw.LanguageService;
import ie.gmit.sw.LiveDataFileReader;
import ie.gmit.sw.Utilities;
import ie.gmit.sw.VectorFileCreator;
import ie.gmit.sw.ngram.NGramProcessor;
import ie.gmit.sw.nn.LanguageNeuralNetwork;
import ie.gmit.sw.vector.SimpleNGramVectoriser;
import org.encog.Encog;
import org.encog.ml.data.MLDataSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MenuModel {

    private int _vectorLength = 1500;
    private int _minNGrams = 2;
    private int _maxNGrams = 2;
    private int _numOutputs = 235;

    private VectorFileCreator _vectorFileCreator;
    private NGramProcessor _nGramProcessor;
    private SimpleNGramVectoriser _vectoriser;

    private String _inputFilePath = "./resources/wili-2018-Small-11750-Edited.txt";
    private String _vectorFilePath;
    private MLDataSet _trainingDataSet;

    private LanguageNeuralNetwork _languageNeuralNetwork;
    private LiveDataFileReader _liveDataFileReader;

    public boolean isNetworkTrained() {
        if (_languageNeuralNetwork != null) {
            return _languageNeuralNetwork.getIsTrained();
        }
        return false;
    }

    public void loadDataSet(File file) {
        _trainingDataSet = Utilities.loadDataSet(file.getName(), _vectorLength, _numOutputs);
    }


    public void createVectorFile() throws IOException {
        // Create the NGramProcessor and Vectoriser
        System.out.println("\tCreating NGram Processor...");
        _nGramProcessor = new NGramProcessor(_minNGrams, _maxNGrams);
        System.out.println("\tCreating Vectoriser...");
        _vectoriser = new SimpleNGramVectoriser();
        // Create the CSV vector file.
        _vectorFileCreator = new VectorFileCreator(_nGramProcessor, _vectoriser);
        System.out.println("\tBuilding NGrams and vector data...");
        _vectorFilePath = _vectorFileCreator.build(_inputFilePath, _vectorLength);
        System.out.println(String.format("\tVector file created at: %s", _vectorFilePath));
        System.out.println("\tLoading Vector data into training set");
        System.out.println("\tFinished!");

        loadDataSet(new File(_vectorFilePath));

        // Create a matching live data reader.
        _liveDataFileReader = new LiveDataFileReader(_nGramProcessor, _vectoriser, _vectorLength);
    }

    public void buildNeuralNetwork() {
        if (_trainingDataSet != null) {
            _languageNeuralNetwork = new LanguageNeuralNetwork(_trainingDataSet);
            _languageNeuralNetwork.setNGrams(_minNGrams, _maxNGrams);
        } else {
            System.out.println("No Training data has been loaded!");
        }
    }

    public void trainNeuralNetwork() {
        if (_languageNeuralNetwork != null) {
            _languageNeuralNetwork.train();
        } else {
            System.out.println("Network has not been created!");
        }
    }

    public void saveNeuralNetwork(String filename) {
        if (_languageNeuralNetwork == null) {
            System.out.println("No network to save!");
        } else {
            if (isNetworkTrained()) {
                _languageNeuralNetwork.saveToFile(filename);
            } else {
                System.out.println("Network has not been trained!");
            }
        }
    }

    public void loadNeuralNetwork(File file) {
        if (_trainingDataSet != null) {
            _languageNeuralNetwork = new LanguageNeuralNetwork(Utilities.loadNeuralNetwork(file), _trainingDataSet);
        } else {
            System.out.println("Training data must be loaded first!");
        }
    }

    public void validateNetwork() {
        if (_trainingDataSet != null) {
            _languageNeuralNetwork.validate();
        } else {
            System.out.println("Training data not present!");
        }
    }

    public void classifyDirectory(File directory) throws IOException {
        for (File f : directory.listFiles()) {
            classifyFile(f);
        }
    }

    public void classifyFile(File file) throws IOException {
        // must be vectorised...
        int result = _languageNeuralNetwork.classify(_liveDataFileReader.fileAsVector(file));

        System.out.println(String.format("File: %s, %s", file.getName(), LanguageService.getLanguages()[result]));
    }

    public void shutdown() {
        Encog.getInstance().shutdown();
    }

    public String printCurrentConfig() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------------------------------------");
        sb.append("\nCurrent Configuration:");
        sb.append("\n---------------------------------------------------------------");
        sb.append(String.format("\n|   Path to WiLI file | %s", _inputFilePath));
        sb.append(String.format("\n|       Vector Length | %d", _vectorLength));
        sb.append(String.format("\n|  Minimum NGram size | %d", _minNGrams));
        sb.append(String.format("\n|  Maximum NGram size | %d", _maxNGrams));
        sb.append(String.format("\n|        Training Set | %s", _trainingDataSet != null ? "Yes" : "Not Set"));
        sb.append(String.format("\n|     Network Created | %s", _languageNeuralNetwork != null ? "Yes" : "No"));
        sb.append(String.format("\n|     Network Trained | %s", isNetworkTrained() ? "Yes" : "No"));

        return sb.toString();
    }

    public void writeToCsv(String filename) {
        try {
            _languageNeuralNetwork.writeNetworkDataToCSV(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setMaxTime(int maxTime) {
        if (_languageNeuralNetwork != null) {
            _languageNeuralNetwork.set_maxSeconds(maxTime);
        }
    }

    public void setMaxEpochs(int maxEpochs) {
        if (_languageNeuralNetwork != null) {
            _languageNeuralNetwork.set_maxEpochs(maxEpochs);
        } else {
            System.out.println("Please create a network first!");
        }
    }

    public void setVectorLength(int vLength) {
        _vectorLength = vLength;
    }

    public void setMinError(int minError) {
        if (_languageNeuralNetwork != null) {
            _languageNeuralNetwork.set_minErrorRate(minError);
        } else {
            System.out.println("Please create a network first!");
        }
    }
}
