package ie.gmit.sw.nn;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class NetworkData {

    private int _maxNGram;
    private int _minNGram;
    private int _vectorPrecision;
    private int _vectorLength;
    private List<EpochData> _epochs;
    private double _trainingAccuracy;
    private double _testAccuracy;

    public NetworkData(int vectorLength, int vectorPrecision, int minNGram, int maxNGram) {

        _vectorPrecision = vectorPrecision;
        _minNGram = minNGram;
        _maxNGram = maxNGram;

        this._vectorLength = vectorLength;

        _epochs = new ArrayList<>();
    }

    public void setTrainingAccuracy(double trainingAccuracy) {
        this._trainingAccuracy = trainingAccuracy;
    }

    public void setTestAccuracy(double testAccuracy) {
        this._testAccuracy = testAccuracy;
    }

    public void addEpochData(EpochData epochData) {
        this._epochs.add(epochData);
    }

    public void writeToCsv(String outFile) throws FileNotFoundException {

        System.out.println("Writing Network Data to file: " + outFile);
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(outFile, true));

        String rowFormat = String.format("%d,%d & %d, %d, %f, %f, ", _vectorLength, _minNGram, _maxNGram, _vectorPrecision, _trainingAccuracy, _testAccuracy);

        for (EpochData e : _epochs) {
            printWriter.append(rowFormat + e.toRowData() + "\n");
        }
        printWriter.close();
    }
}