package ie.gmit.sw.ui;

import java.util.Scanner;

public class MainMenu {

    private Scanner scanner;
    private boolean keepRunning = true;
    private String separator = "---------------------------------------";

    public MainMenu() {
        scanner = new Scanner(System.in);
        runMainUI();
    }

    private void runMainUI() {
        String lastInput = "";

        while (keepRunning) {

            printOptions();
            lastInput = scanner.next();
            int input = handleNumericInput(lastInput);
            System.out.println(separator);

            switch (input) {
                case 1:
                    System.out.println("\tSelected 'Build Neural Network'");
                    break;
                case 2:
                    System.out.println("\tDoing some other stuff");
                    break;
                case 0:
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
        }
    }

    private void printOptions() {

        System.out.println(separator);
        System.out.println("Please select an option:");
        System.out.println("\t1. Do some stuff");
        System.out.println("\t2. Do some other stuff");
        System.out.print("Please make a selection: ");
    }

    private int handleNumericInput(String input) {

        int val = Integer.MIN_VALUE;
        try {
            val = Integer.parseInt(input);
        } catch (NumberFormatException ex) {

        }
        return val;
    }

    private class OptionsConfig {
        private int vectorLength;
    }

}
