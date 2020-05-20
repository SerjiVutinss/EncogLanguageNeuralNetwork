package ie.gmit.sw;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

public class Utilities {

    /**
     * Normalizes the hash vector of language k-mer / n-grams to values in a given
     * range. The lower and upper bounds should correspond to the activation function(s)
     * that you are using in your neural network, e.g. Tanh, Sigmoid, etc.
     *
     * @param vector the array of hashed n-grams.
     * @param lower  the lower bound to squash the vector values from, eg. -1 or 0.
     * @param upper  the upper bound to squash the vector values to, e.g. 1.
     * @return the vector of values normalized within the range [lower..upper].
     */
    public static double[] normalize(double[] vector, double lower, double upper) {
        double[] normalized = new double[vector.length];
        double max = Arrays.stream(vector).max().getAsDouble();
        double min = Arrays.stream(vector).min().getAsDouble();

        for (int i = 0; i < normalized.length; i++) {
            normalized[i] = (vector[i] - min) * (upper - lower) / (max - min) + lower;
        }
        return normalized;
    }


    /**
     * Saves an Encog multilayer perceptron to file. Once a neural network has been
     * trained, it can be saved and loaded again when needed. A trained neural network
     * consists of the network topology with a set of fixed weights. Thus, the file size
     * is typically very small. Assuming that you have a neural network defined as
     * follows:
     * <p>
     * BasicNetwork network = new BasicNetwork();
     * network.addLayer(new BasicLayer(null, true, 777));
     * network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 35));
     * network.addLayer(.......);
     * network.addLayer(.......);
     * network.getStructure().finalizeStructure();
     * network.reset();
     * <p>
     * you can save the network using the following syntax:
     * Utilities.saveNeuralNetwork(network, "language-detect.nn");
     *
     * @param network  the instance of BasicNetwork to save.
     * @param fileName the name of the file to save the network to.
     */
    public static void saveNeuralNetwork(BasicNetwork network, String fileName) {
        EncogDirectoryPersistence.saveObject(new File(fileName), network);
    }


    /**
     * Loads a trained multilayer perceptron from a file. As the neural network has already
     * been trained, the model can be deserialized and reused without further training. Use
     * this method as follows:
     * <p>
     * BasicNetwork network = Utilities.loadNeuralNetwork("language-detect.nn");
     *
     * @param fileName the name of the file containing the serialized instance of BasicNetwork.
     * @return
     */
    public static BasicNetwork loadNeuralNetwork(String fileName) {
        return (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(fileName));
    }

    public static int deserialiseCategories(double[] data) {

        for (int i = 0; i < data.length; i++) {
            if (data[i] == 1) return i;
        }
        return -1;
    }



//    private static int getMinimumIndex(double[] arr) {
//        double minValue = Double.MAX_VALUE;
//        int minIndex = Integer.MAX_VALUE;
//        for (int i = 0; i < arr.length; i++) {
//
//            if (arr[i] < minValue) {
//                minValue = arr[i];
//                minIndex = i;
//            }
//        }
//        return minIndex;
//    }
//
//    private static int getMinIndex(double[] arr) {
//        return IntStream.range(0, arr.length).boxed()
//                .min(Comparator.comparingDouble(
//                        Arrays.stream(arr)
//                                .boxed()
//                                .collect(Collectors.toList())::get))
//                .get();  // or throw if empty list
//    }
//
//    private static int getMaxIndex(double[] arr) {
//        return IntStream.range(0, arr.length).boxed()
//                .max(Comparator.comparingDouble(
//                        Arrays.stream(arr)
//                                .boxed()
//                                .collect(Collectors.toList())::get))
//                .get();  // or throw if empty list
//    }
}