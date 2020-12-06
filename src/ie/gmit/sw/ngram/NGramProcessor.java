package ie.gmit.sw.ngram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NGramProcessor {

    public int getMinNGram() {
        return _minNGram;
    }

    public int getMaxNgram() {
        return _maxNgram;
    }

    private final int _minNGram;
    private final int _maxNgram;

    public NGramProcessor(int minNGram, int maxNGram) {
        _minNGram = minNGram;
        _maxNgram = maxNGram;
    }

    public int[] getNGrams(String text) {

        // temp list
        List<Integer> allNGrams = new ArrayList<>();

        // Only keep the letters...
        String cleaned  = text.replaceAll("[^a-zA-Z ]", "");

        String [] wordsUpper = cleaned.split("\\s+");
        String [] wordsLower = cleaned.toLowerCase().split("\\s+");


        // Hash each word
//        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        Arrays.stream(wordsLower).forEach(x -> allNGrams.add(x.hashCode()));
        Arrays.stream(wordsUpper).forEach(x -> allNGrams.add(x.hashCode()));

        try {
            for (int i = _minNGram; i <= _maxNgram; i++) {
                // Split the untouched string into nGrams
                allNGrams.addAll(getNGrams(text, i));
                // Split the string in lower case string into nGrams
                allNGrams.addAll(getNGrams(text.toLowerCase(), i));
                // Also split the cleaned string
                allNGrams.addAll(getNGrams(cleaned, i));
                // And the lower case cleaned string
                allNGrams.addAll(getNGrams(cleaned.toLowerCase(), i));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Hash all words
        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        Arrays.stream(words).forEach(x -> allNGrams.add(x.hashCode()));

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
