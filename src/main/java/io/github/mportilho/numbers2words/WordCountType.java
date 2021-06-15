package io.github.mportilho.numbers2words;

public enum WordCountType {

    SINGULAR("singular"), PLURAL("plural");

    private final String label;

    private WordCountType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
