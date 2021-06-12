package io.github.mportilho.numbers2words;

import java.util.Properties;

public class Numbers2WordsOptions {

    private boolean showDecimalUnit;
    private boolean showZeroInteger;
    private boolean appendSingularUnitToZero;
    private boolean showIntegerUnitWhenNoDecimalFound;
    private boolean identifyOmittedIntegerUnitWhenNoIntegerFound;
    
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
