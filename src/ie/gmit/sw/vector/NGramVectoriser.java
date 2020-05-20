package ie.gmit.sw.vector;

public interface NGramVectoriser {

    double[] vectorise(int[] nGrams, int vectorLength);
}
