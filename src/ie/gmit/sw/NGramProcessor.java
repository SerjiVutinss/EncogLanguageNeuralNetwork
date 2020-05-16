package ie.gmit.sw;

import java.util.ArrayList;
import java.util.List;

public class NGramProcessor {

    public static int[] getNGrams(String text, int lower, int upper) {
        List<Integer> allNGrams = new ArrayList<>();
        try {
            for (int i = lower; i <= upper; i++) {
                allNGrams.addAll(getNGrams(text, i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allNGrams.stream().mapToInt(i -> i).toArray();
    }

    private static List<Integer> getNGrams(String text, int ngramLength) throws Exception {

        List<Integer> nGrams = new ArrayList<>();
        text = text.replace('\n', ' ');

        String ngram;
        for (int i = 0; i < text.length() - ngramLength + 1; i++) {
            int charsRemaining = text.length() - i;
            if (charsRemaining < ngramLength) {
                ngram = text.substring(i, i + charsRemaining);
            } else {
                ngram = text.substring(i, i + ngramLength);
            }
            nGrams.add(ngram.hashCode());
        }
        return nGrams;
    }

}
