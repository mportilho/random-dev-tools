package io.github.mportilho.numbers2words;

import java.util.Objects;
import java.util.Properties;

public final class N2WOptionsBuilder {

    private boolean displayingIntegerUnit;
    private boolean displayingDecimalUnit;
    private boolean displayingDecimalScale;
    private boolean displayingZeroInteger;
    private boolean displayingSingularWord;
    private boolean appendingSingularUnitToZero;
    private boolean identifyOmittedIntegerUnitWhenNoIntegerFound;

    private WordGender gender;
    private Properties properties;

    private N2WOptionsBuilder() {

    }

    public static N2WOptionsBuilder create(WordGender gender, Properties properties) {
        N2WOptionsBuilder builder = new N2WOptionsBuilder();
        builder.properties = Objects.requireNonNull(properties, "Properties cannot be null");
        builder.gender = gender;
        return builder;
    }

    public Numbers2WordsOptions build() {
        return new Numbers2WordsOptions(properties, gender, displayingIntegerUnit, displayingDecimalUnit, displayingDecimalScale,
                displayingZeroInteger, displayingSingularWord, appendingSingularUnitToZero);
    }

    public N2WOptionsBuilder setDecimalSeparator(String value) {
        this.properties.setProperty("decimal_separator", value);
        return this;
    }

    public N2WOptionsBuilder setThousandsSeparator(String value) {
        this.properties.setProperty("thousands_separator", value);
        return this;
    }

    public N2WOptionsBuilder setNumberSeparator(String value) {
        this.properties.setProperty("number_separator", value);
        return this;
    }

    public N2WOptionsBuilder setIntegerSuffix(WordGender gender, WordCountType type, String value) {
        StringBuilder strBuilder = new StringBuilder("integer.suffix");
        if (gender != null && type != null) {
            strBuilder.append('.').append(gender.getLabel()).append('.').append(type.getLabel());
        } else if (gender != null) {
            strBuilder.append('.').append(gender.getLabel());
        } else if (type != null) {
            strBuilder.append('.').append(type.getLabel());
        }
        this.properties.setProperty(strBuilder.toString(), value);
        return this;
    }

    public N2WOptionsBuilder setFractionSuffix(WordGender gender, WordCountType type, String value) {
        StringBuilder strBuilder = new StringBuilder("fraction.suffix");
        if (gender != null && type != null) {
            strBuilder.append('.').append(gender.getLabel()).append('.').append(type.getLabel());
        } else if (gender != null) {
            strBuilder.append('.').append(gender.getLabel());
        } else if (type != null) {
            strBuilder.append('.').append(type.getLabel());
        }
        this.properties.setProperty(strBuilder.toString(), value);
        return this;
    }

    public N2WOptionsBuilder displayingIntegerUnit(boolean displayingIntegerUnit) {
        this.displayingIntegerUnit = displayingIntegerUnit;
        return this;
    }

    public N2WOptionsBuilder displayingDecimalUnit(boolean displayingDecimalUnit) {
        this.displayingDecimalUnit = displayingDecimalUnit;
        return this;
    }

    public N2WOptionsBuilder displayingDecimalScale(boolean displayingDecimalScale) {
        this.displayingDecimalScale = displayingDecimalScale;
        return this;
    }

    public N2WOptionsBuilder displayingZeroInteger(boolean displayingZeroInteger) {
        this.displayingZeroInteger = displayingZeroInteger;
        return this;
    }

    public N2WOptionsBuilder displayingSingularWord(boolean displayingSingularWord) {
        this.displayingSingularWord = displayingSingularWord;
        return this;
    }

    public N2WOptionsBuilder appendingSingularUnitToZero(boolean appendingSingularUnitToZero) {
        this.appendingSingularUnitToZero = appendingSingularUnitToZero;
        return this;
    }

    public N2WOptionsBuilder identifyOmittedIntegerUnitWhenNoIntegerFound(boolean identifyOmittedIntegerUnitWhenNoIntegerFound) {
        this.identifyOmittedIntegerUnitWhenNoIntegerFound = identifyOmittedIntegerUnitWhenNoIntegerFound;
        return this;
    }

    public boolean isDisplayingDecimalUnit() {
        return displayingDecimalUnit;
    }

    public boolean isDisplayingZeroInteger() {
        return displayingZeroInteger;
    }

    public boolean isAppendingSingularUnitToZero() {
        return appendingSingularUnitToZero;
    }

    public boolean isIdentifyOmittedIntegerUnitWhenNoIntegerFound() {
        return identifyOmittedIntegerUnitWhenNoIntegerFound;
    }

    public boolean isDisplayingIntegerUnit() {
        return displayingIntegerUnit;
    }

    public boolean isDisplayingDecimalScale() {
        return displayingDecimalScale;
    }

    public WordGender getGender() {
        return gender;
    }
}
