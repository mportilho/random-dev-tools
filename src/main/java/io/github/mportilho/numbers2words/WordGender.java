package io.github.mportilho.numbers2words;

public enum WordGender {

    MASCULINE("masculine"), FEMININE("feminine");

    private String label;

    WordGender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

}
