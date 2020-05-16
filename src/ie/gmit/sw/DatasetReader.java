package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatasetReader {

    public static void Read(String filePath) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String st;
        while ((st = br.readLine()) != null)
            System.out.println(st);
    }

    public static Tuple[] ReadTuples(String filePath) throws IOException {

        List<Tuple<String, String>> splitLines = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        Tuple<String, String> lineSplit;
        while ((line = br.readLine()) != null) {

            lineSplit = splitLine(line);
            if (lineSplit != null) {
                splitLines.add(splitLine(line));
            }
        }
        return splitLines.toArray(new Tuple[0]);
    }

    public static Tuple<String, String> splitLine(String line) {

        String[] split = line.split("@");

        if (split.length > 2) return null;

        return new Tuple<>(split[0], split[1]);
    }
}
