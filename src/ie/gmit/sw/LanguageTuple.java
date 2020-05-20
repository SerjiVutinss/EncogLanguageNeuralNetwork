package ie.gmit.sw;

public class LanguageTuple {

    String _text;
    int _languageIndex;

    public LanguageTuple() {
    }

    public LanguageTuple(String t, int u) {
        _text = t;
        _languageIndex = u;
    }

    public String getText() {
        return _text;
    }

    public int getLanguageIndex() {
        return _languageIndex;
    }

}