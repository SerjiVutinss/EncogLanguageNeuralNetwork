package ie.gmit.sw;

import java.util.HashMap;
import java.util.Map;

public class LanguageService {

    private static Language[] langs = getLanguages();
    private static Map<Language, Integer> _languageMap;
    private static Map<String, Language> _stringMap;

    private static void init() {

        _languageMap = new HashMap();
        _stringMap = new HashMap();

        langs = Language.values();

        for (int i = 0; i < langs.length; i++) {
            _languageMap.put(langs[i], i);
            _stringMap.put(langs[i].toString(), langs[i]);
        }
    }

    public static Language[] getLanguages() {

        if (langs == null) init();

        Language[] langCopy = new Language[langs.length];

        for (int i = 0; i < langs.length; i++) langCopy[i] = langs[i];

        return langCopy;
    }

    public static int getLanguageIndex(String language) {
        try {
            return _languageMap.get(_stringMap.get(language));
        } catch (NullPointerException ex) {
            return -1;
        }
    }

}
