package ie.gmit.sw.vector;

import java.util.Arrays;

public class SimpleNGramVectoriser implements NGramVectoriser {

    public double[] vectorise(int[] nGrams, int vectorLength) {

        double[] vector = new double[vectorLength];

        Arrays.stream(vector).map(x -> 0);
        Arrays.stream(nGrams).forEach(x -> {
            vector[Math.abs(x % vectorLength)]++;
        });

        for (int n : nGrams) {
            vector[Math.abs(n % vectorLength)]++;
        }
        return vector;
    }
}
