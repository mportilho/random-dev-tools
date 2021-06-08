package io.github.mportilho.numbers2words;

import java.util.Properties;

public class Numbers2WordsOptions {

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

    private final WordGender gender;
    private final Properties properties;

    public Numbers2WordsOptions(WordGender gender, Properties properties) {
        this.gender = gender;
        this.properties = properties;
    }

    public WordGender getGender() {
        return gender;
    }

    public Properties getProperties() {
        return properties;
    }
}
