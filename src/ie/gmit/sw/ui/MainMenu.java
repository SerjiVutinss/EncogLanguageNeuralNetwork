package ie.gmit.sw.ui;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MainMenu {

    private MenuModel _menuModel;

    private Scanner scanner;
    private boolean keepRunning = true;
    private String separator = "---------------------------------------------------------------";

    public MainMenu() {
        _menuModel = new MenuModel();
        scanner = new Scanner(System.in);
        runMainUI();
    }

    private void runMainUI() {
        String lastInput = "";

        do {
            System.out.println(_menuModel.printCurrentConfig());

            printOptions();
            lastInput = scanner.next();
            int input = handleNumericInput(lastInput);

            System.out.println(separator);

            switch (input) {

                // Part 1 - build and validate
                case 1:
                    try {
                        _menuModel.createVectorFile();
                    } catch (IOException e) {
                        System.out.println("File not found, please check path");
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("Selected 'Build Neural Network'");
                    _menuModel.buildNeuralNetwork();
                    break;
                case 3:
                    System.out.println("Selected 'Train Neural Network'");
                    _menuModel.trainNeuralNetwork();
                    break;
                case 4:
                    System.out.println("Selected 'Validate Neural Network against Training Set'");
                    _menuModel.validateNetwork();
                    break;

                // Part 2 - Test the Network
                case 5:
                    System.out.println("Selected 'Test Single File'");

                    if (_menuModel.isNetworkTrained()) {

                        System.out.println("Please enter path to test file: ");
                        String filePath = scanner.next();

                        File f = getFileOrDirectory(filePath, false);

                        if (f != null) {
                            try {
                                _menuModel.classifyFile(f);
                            } catch (IOException e) {
                                printFileProcessError();
                            }
                        } else {
                            printFileNotFound(filePath, false);
                        }

                    } else {
                        printNetworkNotTrained();
                    }
                    break;

                case 6:
                    System.out.println("Selected 'Test Folder'");

                    if (_menuModel.isNetworkTrained()) {

                        System.out.println("Please enter path to test folder: ");
                        String folderPath = scanner.next();

                        File f = getFileOrDirectory(folderPath, true);

                        if (f != null) {
                            try {
                                _menuModel.classifyDirectory(f);
                            } catch (IOException e) {
                                printFileProcessError();
                            }
                        } else {
                            printFileNotFound(folderPath, true);
                        }

                    } else {
                        printNetworkNotTrained();
                    }
                    break;

                // Part 3 - Save/Load Dataset/Network
                case 7:
                    System.out.println("Selected 'Load DataSet' from file");

                    System.out.println("Please enter path of file to load network from: ");
                    String datasetFilePath = scanner.next();

                    File dataSetFile = getFileOrDirectory(datasetFilePath, false);

                    if (dataSetFile != null) {
                        _menuModel.loadDataSet(dataSetFile);
                    } else {
                        printFileNotFound(datasetFilePath, false);
                    }

                    break;

                case 8:
                    System.out.println("Selected 'Save Network to file'");

                    if (_menuModel.isNetworkTrained()) {

                        System.out.println("Please enter path to save the file: ");
                        String saveFilePath = scanner.next();

                        _menuModel.saveNeuralNetwork(saveFilePath);
                        System.out.println("Network saved to: " + saveFilePath);

                    } else {
                        printNetworkNotTrained();
                    }
                    break;

                case 9:
                    System.out.println("Selected 'Load Network from file'");

                    System.out.println("Please enter path of file to load network from: ");
                    String filePath = scanner.next();

                    File f = getFileOrDirectory(filePath, false);

                    if (f != null) {
                        _menuModel.loadNeuralNetwork(f);
                    } else {
                        printFileNotFound(filePath, false);
                    }
                    break;
                case 10:
                    System.out.println("Selected 'Set Vector Length'");
                    System.out.println("Please enter vector length:");
                    String v = scanner.next();
                    int vLength = handleNumericInput(v);

                    if (vLength != Integer.MIN_VALUE) {
                        _menuModel.setVectorLength(vLength);
                        System.out.println("Vector Length set to " + vLength);
                    } else {
                        System.out.println("Not a valid vector length, must be an number!");
                    }


                    break;

                case 11:
                    System.out.println("Selected 'Set Max Epochs'");
                    System.out.println("Please enter maximum number of epochs:");
                    String sMaxEpochs = scanner.next();
                    int maxEpochs = handleNumericInput(sMaxEpochs);

                    if (maxEpochs != Integer.MIN_VALUE) {
                        _menuModel.setMaxEpochs(maxEpochs);
                        System.out.println("Max Epochs set to: " + maxEpochs);
                    } else {
                        System.out.println("Not valid, must be a number!");
                    }
                    break;

                case 12:
                    System.out.println("Selected 'Set Max Time'");
                    System.out.println("Please enter maximum training time in seconds:");
                    String sMaxTime = scanner.next();
                    int maxTime = handleNumericInput(sMaxTime);

                    if (maxTime != Integer.MIN_VALUE) {
                        _menuModel.setMaxTime(maxTime);
                        System.out.println("Max Training Time set to " + maxTime);
                    } else {
                        System.out.println("Not a time, must be a number!");
                    }
                    break;

//                case 13:
//                    System.out.println("Selected 'Set Min Error'");
//                    System.out.println("Please enter minimum epoch error:");
//                    String sMinError = scanner.next();
//                    int minError = handleNumericInput(sMinError);
//
//                    if (minError != Integer.MIN_VALUE) {
//                        _menuModel.setMinError(minError);
//                        System.out.println("Minimum Error set to " + minError);
//                    } else {
//                        System.out.println("Not a error value, must be a number!");
//                    }
//                    break;

                case 0:
                    System.out.println("Shutting down Encog");
                    _menuModel.shutdown();

                    System.out.println("Exiting");
                    keepRunning = false;
                    break;

                case Integer.MIN_VALUE:
                    System.out.println("\tOnly numerical input is allowed, please try again.");
                    break;
                default:
                    System.out.println(String.format("\t%s is an unknown option, please try again.", lastInput));
                    break;
            }
            System.out.println();

        } while (keepRunning);
    }

    private void printOptions() {

        System.out.println(separator);
        System.out.println("| Please select an option:");
        System.out.println("|  Part 1. Build and Validate a Network:");
        System.out.println("|\t1. Build training data with current configuration");
        System.out.println("|\t2. Build new Neural Network with current configuration");
        System.out.println("|\t3. Train Neural Network");
        System.out.println("|\t4. Validate Neural Network against Training Set");
        System.out.println("|");
        System.out.println("|  Part 2. Test the Network:");
        System.out.println("|\t5. Test Single File");
        System.out.println("|\t6. Test Folder");
        System.out.println("|");
        System.out.println("|  Part 3. Save/Load DataSet/Network:");
        System.out.println("|\t7. Load DataSet from CSV file");
        System.out.println("|\t8. Save Network to file");
        System.out.println("|\t9. Load Network from file");
        System.out.println("|");
        System.out.println("|  Part 4. Set Parameters:");
        System.out.println("|\t10. Set Vector Length");
        System.out.println("|\t11. Set Max Epochs");
        System.out.println("|\t12. Set Max Time");
//        System.out.println("|\t13. Set Min Error");

        System.out.println("|\t0. Exit");
        System.out.println(separator);

        System.out.print("Please make a selection: ");
    }

    private void printNetworkNotTrained() {
        System.out.println("Network does not exists or has not been trained!");
    }

    private void printFileNotFound(String path, boolean isDirectory) {
        String type = isDirectory ? "directory" : "file";
        String checkDir = " and it is not a directory";
        System.out.println(String.format("Could not find %s: %s, please check that it exists%s.", type, path, checkDir));
    }

    private void printFileProcessError() {
        System.out.println("Something went wrong processing the input file... skipping.");
    }

    private class OptionsConfig {
        private int vectorLength;
    }

    private static File getFileOrDirectory(String filename, boolean isDirectory) {
        File f = new File(filename);
        return (f.exists() && f.isDirectory() == isDirectory) ? f : null;
    }

    private static int handleNumericInput(String input) {

        int val = Integer.MIN_VALUE;
        try {
            val = Integer.parseInt(input);
        } catch (NumberFormatException ex) {

        }
        return val;
    }

}
