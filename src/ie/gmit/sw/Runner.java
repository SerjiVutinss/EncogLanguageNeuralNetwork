package ie.gmit.sw;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

public class Runner {
    public static void main(String[] args) throws IOException {


//            String input = "HelloWorld";
//
//            var ngrams = TestNGramProcessor.getNGrams(input, 1, 4);
//            for(var n : ngrams){
//                System.out.println(n);
//            }

        vectoriseDataset();

    }

    public static void vectoriseDataset() throws IOException {

        boolean isDebug = true;
        String filePath = "./datasets/wili-2018-Small-11750-Edited.txt";
//        String filePath = "./wili-2018-Large-117500-Edited.txt";

        Language[] langs = Language.values();

        int vectorLength = 20;
        int minNgram = 2;
        int maxNgram = 10;
        System.out.println("Vector Length: " + vectorLength);
        System.out.println("Smallest NGram size: " + minNgram);
        System.out.println("Largest NGram size: " + maxNgram);

        // Create a decimal formatter to format the doubles
        DecimalFormat formatter = new DecimalFormat("#0.00000000");

        PrintWriter writer = new PrintWriter("./vector_data/wili_" + vectorLength + ".csv", "UTF-8");

        // Helper to add language names to CSV
        if (isDebug) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < vectorLength; i++) {
                sb.append("v" + i + ",");
            }
            for (Language lang : langs) {
                sb.append(lang.toString() + ",");
            }
            writer.println(sb.substring(0, sb.length() - 1));
        }

        // For each pair of text and language read from the file
        for (Tuple<String, String> t : DatasetReader.ReadTuples(filePath)) {

            // Get the hashes of all desired nGrams
            int[] nGrams = NGramProcessor.getNGrams(t._a, minNgram, maxNgram);

            // Vectorise the hashes
            double[] vector = VectorHash.vectorHash(nGrams, vectorLength);

            // Create a SB to store the CsvString
            StringBuilder sb = new StringBuilder();

            vector = Utilities.normalize(vector, -1, 1);

            for (double v : vector) {
                sb.append(formatter.format(v) + ",");
            }

            // Finally append all of the languages
            for (Language s : langs) {
                if (t._b.equals(s.toString())) {
                    sb.append(1 + ",");
                } else {
                    sb.append(0 + ",");
                }
            }
            writer.println(sb.substring(0, sb.length() - 1));
        }

        writer.close();
    }
}