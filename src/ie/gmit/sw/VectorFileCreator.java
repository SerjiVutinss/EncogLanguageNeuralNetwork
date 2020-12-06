package ie.gmit.sw;

import ie.gmit.sw.ngram.NGramProcessor;
import ie.gmit.sw.vector.NGramVectoriser;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;

public class VectorFileCreator {

    private DecimalFormat formatter = new DecimalFormat("#0.000");

    private NGramVectoriser _nGramVectoriser;
    private NGramProcessor _nGramProcessor;

    public VectorFileCreator(NGramProcessor nGramProcessor, NGramVectoriser nGramVectoriser) {

        _nGramProcessor = nGramProcessor;
        _nGramVectoriser = nGramVectoriser;
    }

    public String build(String inFile, int vectorLength) throws IOException {
        return build(inFile, vectorLength, DatasetType.SMALL);
    }

    public String build(String inFile, int vectorLength, DatasetType datasetType) throws IOException {

//        String outFile = buildFilename(vectorLength, datasetType, _nGramProcessor.getMinNGram(), _nGramProcessor.getMaxNgram());

        String outFile = String.format("./vectors-v%d-n%d-%d.csv", vectorLength, _nGramProcessor.getMinNGram(), _nGramProcessor.getMaxNgram());

        int lowerNormal = 0;
        int upperNormal = 1;

        // Get the raw file data as (text, language) pairs
        LanguageTuple[] data = WiLiDatasetReader.ReadTuples(inFile);

        PrintWriter writer = new PrintWriter(outFile, "UTF-8");

        int[] nGrams;
        double[] vector;
        double[] normalised;
        // For each pair of text and language read from the file
        for (LanguageTuple t : data) {

            // Get the hashes of all desired nGrams
            nGrams = _nGramProcessor.getNGrams(t._text);

            // Vectorise the hashes
            vector = _nGramVectoriser.vectorise(nGrams, vectorLength);

            // Normalise the vectors
            normalised = Utilities.normalize(vector, lowerNormal, upperNormal);

            writer.println(ToCsvRow(normalised, t._languageIndex));
        }

        return outFile;
    }

    public String ToCsvRow(double[] data, int categoryIndex) {
        StringBuilder sb = new StringBuilder();

        Arrays.stream(data).forEach(x -> sb.append(formatter.format(x) + ","));

        for (int i = 0; i < LanguageService.getLanguages().length; i++) {
            if (i == categoryIndex) sb.append(1.0 + ",");
            else sb.append(0.0 + ",");
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    public static String buildFilename(int vectorLength, DatasetType datasetType, int minNgram, int maxNgram) {

        String folder = "./data/vector";

        return String.format("%s/%s-v%d-n%d-n%d.csv", folder, datasetType, vectorLength, minNgram, maxNgram);

    }

}
