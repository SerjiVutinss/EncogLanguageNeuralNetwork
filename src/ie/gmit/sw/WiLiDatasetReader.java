package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WiLiDatasetReader {

    public static LanguageTuple[] ReadTuples(String filePath) throws IOException {

        List<LanguageTuple> splitLines = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        LanguageTuple lineSplit;
        while ((line = br.readLine()) != null) {
            lineSplit = splitLine(line);
            if (lineSplit != null) {
                splitLines.add(lineSplit);
            }
        }
        return splitLines.toArray(new LanguageTuple[0]);
    }

    public static LanguageTuple splitLine(String line) {

        String[] split = line.split("@");
        if (split.length > 2) return null;

        int langIndex = LanguageService.getLanguageIndex(split[1].trim());

        if (langIndex >= 0) {
            return new LanguageTuple(split[0], langIndex);
        }
        return null;

    }
}
