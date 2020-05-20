package ie.gmit.sw;

import ie.gmit.sw.nn.WiliNeuralNetwork;
import ie.gmit.sw.ui.MainMenu;
import ie.gmit.sw.vector.SimpleNGramVectoriser;
import org.encog.neural.networks.BasicNetwork;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Runner {

    static int vectorLength = 1600;

    static String networkResultsFile;
    private static NGramProcessor _nGramProcessor;
    private static SimpleNGramVectoriser _vectoriser;

    public static void main(String[] args) throws IOException {

//        var now = LocalDateTime.now();
//        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
//        networkResultsFile = "./data/network/network_data_" + dtFormat.format(now) + ".csv";
//
//        test();

        new MainMenu();

    }

    private static BasicNetwork loadNetwork(String filename) {
        return Utilities.loadNeuralNetwork(filename);
    }

    private static void test() throws IOException {

        int minNGram = 2;
        int maxNGram = 2;

//        String filePath = "./data/datasets/wili-2018-Small-11750-Edited.txt";
        String largeDatasetFilePath = "./data/datasets/wili-2018-Large-Less-Small-105750-Edited.txt";
        String smallDatasetFilePath = "./data/datasets/wili-2018-Small-11750-Edited.txt";
//
//        var trainingSet = createDataset(largeDatasetFilePath, vectorLength, DatasetType.LARGE, minNgram, maxNgram);
//        var testSet = createDataset(smallDatasetFilePath, vectorLength, DatasetType.SMALL, minNgram, maxNgram);

        var largeVectorSetFilePath = DatasetBuilder.buildFilename(vectorLength, DatasetType.LARGE, minNGram, maxNGram);
        var smallVectorSetFilePath = DatasetBuilder.buildFilename(vectorLength, DatasetType.SMALL, minNGram, maxNGram);

        _nGramProcessor = new NGramProcessor(minNGram, maxNGram);
        _vectoriser = new SimpleNGramVectoriser();

        // build the network
//        WiliNeuralNetwork wiliNeuralNetwork = new WiliNeuralNetwork(largeVectorSetFilePath, vectorLength, LanguageService.getLanguages().length, minNGram, maxNGram);
//        wiliNeuralNetwork.setTrainingDataSetType(DatasetType.SMALL);
//        wiliNeuralNetwork.build();
////
//        int epochs = wiliNeuralNetwork.train();

//         Load a network
        WiliNeuralNetwork wiliNeuralNetwork = new WiliNeuralNetwork(largeVectorSetFilePath, vectorLength, LanguageService.getLanguages().length, minNGram, maxNGram);
        wiliNeuralNetwork.setTrainingDataSetType(DatasetType.SMALL);
        wiliNeuralNetwork.build();
        wiliNeuralNetwork.loadNetwork("./my-nn.nn");


//
//
////        wiliNeuralNetwork.saveNetwork("./wili_567.nn");
////        wiliNeuralNetwork.loadNetwork("./wili_567.nn");
//
//
//        System.out.println("Testing network with test data: " + smallDatasetFilePath);
//        wiliNeuralNetwork.printTestResults(false);

//        System.out.println("Testing network with training data: " + largeDatasetFilePath);
//        wiliNeuralNetwork.printTestResults(true);


//        String[] testStrings = new String[]{
//                // English
//                "One of the limiting features of multi-layer perceptron is that the number of input neurons is fixed, causing issues when dealing with variable-length input sources such as text. Once way to circumvent this problem is to hash the inputs to a fixed-size vector and then map each of the vector indices to a node in the input layer of a neural network. When using text as a source, a common practice is to first decompose the text into a set of n-grams and then hash each of these. You are required to use the Encog 3.2 API to develop a multilayer neural network capable of detecting the language of text in a file. The text should be parsed, converted into n-grams and then hash"
//                // Spanish
//                , "Mi familia no es muy grande. Somos solo cuatro personas: mi padre, mi madre, mi hermana, y yo. También tenemos un perro. Yo soy mayor que mi hermana, pero ella es más alta. Vamos juntos al colegio. Yo tengo doce años y ella once. Mis padres se llaman Javier y María. Mi hermana se llama Sara, y yo Luis. Vivimos en una casa muy bonita."
//                // German
//                , "Wenn die beiden ihre Schule beendet haben, wollen sie in Deutschland studieren. Juliana will Tierärztin werden, ihre beste Freundin auch. Aber Maries Eltern sind beide Zahnärzte, deshalb wird Marie wahrscheinlich auch Zahnärztin werden."
//                // Dutch
//                , "Dan gaat hij naar de viskraam. De vis ligt op ijs in bakken. Het ijs houdt de vis vers. Frank kiest een grote vis uit. Dat kost twee euro, zegt de visboer. Frank geeft hem een biljet van vijf euro om te betalen. De visboer geeft hem drie euro terug. De verkoper verpakt in papier. Frank stopt de vis in de kleine tas."
//                // Polish
//                , "Na każdym w przyjęciu urodzinowym bardzo ważny jest tort. Mama Mateusza zamawia tort w sklepie, zawsze jednak Mateusz starannie wybiera jak ma on wyglądać. Zazwyczaj na torcie namalowane są ulubione postacie z bajek. Czasem są to samochody, czasem roboty, czasem zwierzęta- co roku tort jest inny. Prawie zawsze jednak smakuje podobnie, gdyż Mateusz uwielbia czekoladowe torty."
//                // Russian
//                , "Встре́ча с медве́дем мо́жет быть о́чень опа́сна. Ру́сские лю́ди лю́бят ходи́ть в лес и собира́ть грибы́ и я́годы. Они́ де́лают э́то с осторо́жностью, так как медве́ди то́же о́чень лю́бят я́годы и мо́гут напа́сть на челове́ка. Медве́дь ест всё: я́годы, ры́бу, мя́со и да́же насеко́мых. Осо́бенно он лю́бит мёд."
//        };
//
//        for (var s : testStrings) {
//            var vector = getStringAsVector(s);
//            wiliNeuralNetwork.testVector(vector);
//        }

//                "./data/live_data"
        String[] testFiles = {
                "Afrikaans_DigtersUitSuidAfrika.txt"
                , "Chinese_ChangHenGeYanJiu.txt"
                , "Irish_BesideTheFire.txt"
                , "Russian_Moscovia.txt"
        };

        File folder = new File("./data/live_data");
        File[] listOfFiles = folder.listFiles();

        LiveDataFileReader ldfr = new LiveDataFileReader(_nGramProcessor, _vectoriser, vectorLength);

        for (var f : listOfFiles) {
            if(f.isFile()) {
                System.out.println();
                wiliNeuralNetwork.testVector(ldfr.fileAsVector(f.getAbsolutePath()));
                System.out.print(" : " + f.getName());
            }
        }


//        for (int i = 0; i < listOfFiles.length; i++) {
//            if (listOfFiles[i].isFile()) {
//                System.out.println("File " + listOfFiles[i].getName());
//            } else if (listOfFiles[i].isDirectory()) {
//                System.out.println("Directory " + listOfFiles[i].getName());
//            }
//        }
//
////
////        // ~ 12 files at 1000 words each...
////
//        LiveDataFileReader ldfr = new LiveDataFileReader(_nGramProcessor, _vectoriser, vectorLength);
//        String testFileDir = "./data/live_data/";
//        for (var f : testFiles) {
//            wiliNeuralNetwork.testVector(ldfr.fileAsVector(testFileDir + f));
//        }


        // Latin scripts like English and Irish, cyrillic like Russian, and more complex Unicode formats like Japanese, Chinese and Arabic


//        wiliNeuralNetwork.writeNetworkDataToCSV(networkResultsFile);

//        wiliNeuralNetwork.saveToFile();

        wiliNeuralNetwork.shutdown();
    }


    private static String createDataset(String filePath, int vectorLength, DatasetType datasetType, int minNGram, int maxNGram) throws IOException {

        DatasetBuilder datasetBuilder = new DatasetBuilder(_nGramProcessor, _vectoriser);

        return datasetBuilder.build(filePath, vectorLength, datasetType);
    }

    private static double[] getStringAsVector(String input) {
        int[] testData = _nGramProcessor.getNGrams(input);

        return Utilities.normalize(_vectoriser.vectorise(testData, vectorLength), 0, 1);
    }
}