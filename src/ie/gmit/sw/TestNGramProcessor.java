package ie.gmit.sw;

import java.util.ArrayList;
import java.util.List;

public class TestNGramProcessor {

    public static String[] getNGrams(String text, int lower, int upper) {

        List<String> allNGrams = new ArrayList<>();
        try {
            for (int i = lower; i <= upper; i++) {
                allNGrams.addAll(getNGrams(text, i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allNGrams.toArray(new String[0]);
    }

    private static List<String> getNGrams(String text, int ngramLength) throws Exception {

        List<String> nGrams = new ArrayList<>();
        text = text.replace('\n', ' ');

        String ngram;


        for (int i = 0; i < text.length() - ngramLength + 1; i++) {
            int charsRemaining = text.length() - i;
            if (charsRemaining < ngramLength) {
                ngram = text.substring(i, i + charsRemaining);
            } else {
                ngram = text.substring(i, i + ngramLength);
            }
            nGrams.add(ngram);
        }
        return nGrams;
    }

}
