package ie.gmit.sw;

import ie.gmit.sw.ngram.NGramProcessor;
import ie.gmit.sw.vector.NGramVectoriser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LiveDataFileReader {

    private final NGramVectoriser _vectoriser;
    private final NGramProcessor _nGramProcessor;
    private final int _vectorLength;

    public LiveDataFileReader(NGramProcessor nGramProcessor, NGramVectoriser vectoriser, int vectorLength) {

        _nGramProcessor = nGramProcessor;
        _vectoriser = vectoriser;
        _vectorLength = vectorLength;

    }

    private double[] getStringAsVector(String input) {
        int[] testData = _nGramProcessor.getNGrams(input);

        return Utilities.normalize(_vectoriser.vectorise(testData, _vectorLength), 0, 1);
    }

    public double[] fileAsVector(File file) throws IOException {

        List<Double> normalised = new ArrayList<>();

        List<Integer> allNGrams = new ArrayList<>();


        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        LanguageTuple lineSplit;
        while ((line = br.readLine()) != null) {

            var lineGrams = _nGramProcessor.getNGrams(line);
            Arrays.stream(lineGrams).forEach(x -> allNGrams.add(x));
        }

        double[] vector = _vectoriser.vectorise(allNGrams.stream().mapToInt(i -> i).toArray(), _vectorLength);

        double[] n = Utilities.normalize(vector, 0, 1);

        return n;

    }

    public double[] fileAsVector(String filename) throws IOException {

        List<Double> normalised = new ArrayList<>();

        List<Integer> allNGrams = new ArrayList<>();


        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        LanguageTuple lineSplit;
        while ((line = br.readLine()) != null) {

            var lineGrams = _nGramProcessor.getNGrams(line);
            Arrays.stream(lineGrams).forEach(x -> allNGrams.add(x));
        }

        double[] vector = _vectoriser.vectorise(allNGrams.stream().mapToInt(i -> i).toArray(), _vectorLength);

        double[] n = Utilities.normalize(vector, 0, 1);

        return n;

    }


}
