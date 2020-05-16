package ie.gmit.sw;

public class VectorHash {

    public static double[] vectorHash(int[] nGrams, int vectorLength) {

        double[] vector = new double[vectorLength];
        for (double d : vector) {
            d = 0;
        }

        for(int n : nGrams) {
            vector[Math.abs(n % vectorLength)]++;
        }

        return vector;
    }

}
