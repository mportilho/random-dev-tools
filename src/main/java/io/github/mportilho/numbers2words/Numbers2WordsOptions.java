package io.github.mportilho.numbers2words;

import io.github.mportilho.numbers2words.i18n.ThousandSeparatorRule;

import java.util.Properties;

public class Numbers2WordsOptions {

    private final Properties properties;
    private final WordGender gender;
    private final ThousandSeparatorRule rule;
    private final boolean displayingIntegerUnit;
    private final boolean displayingDecimalUnit;
    private final boolean displayingDecimalScale;
    private final boolean displayingZeroInteger;
    private final boolean displayZeroFraction;
    private final boolean displayingSingularWord;
    private final boolean appendingSingularUnitToZero;

    protected Numbers2WordsOptions(Properties properties, WordGender gender, ThousandSeparatorRule rule, boolean displayingIntegerUnit,
                                   boolean displayingDecimalUnit, boolean displayingDecimalScale, boolean displayingZeroInteger,
                                   boolean displayZeroFraction, boolean displayingSingularWord, boolean appendingSingularUnitToZero) {
        this.properties = properties;
        this.gender = gender;
        this.rule = rule;
        this.displayingIntegerUnit = displayingIntegerUnit;
        this.displayingDecimalUnit = displayingDecimalUnit;
        this.displayingDecimalScale = displayingDecimalScale;
        this.displayingZeroInteger = displayingZeroInteger;
        this.displayZeroFraction = displayZeroFraction;
        this.displayingSingularWord = displayingSingularWord;
        this.appendingSingularUnitToZero = appendingSingularUnitToZero;
    }

    public WordGender getGender() {
        return gender;
    }

    public Properties getProperties() {
        return properties;
    }

    public ThousandSeparatorRule getRule() {
        return rule;
    }

    public boolean isDisplayingIntegerUnit() {
        return displayingIntegerUnit;
    }

    public boolean isDisplayingDecimalUnit() {
        return displayingDecimalUnit;
    }

    public boolean isDisplayingDecimalScale() {
        return displayingDecimalScale;
    }

    public boolean isDisplayingZeroInteger() {
        return displayingZeroInteger;
    }

    public boolean isDisplayZeroFraction() {
        return displayZeroFraction;
    }

    public boolean isAppendingSingularUnitToZero() {
        return appendingSingularUnitToZero;
    }

    public boolean isDisplayingSingularWord() {
        return displayingSingularWord;
    }

    public String getThousandsSeparator() {
        String property = this.properties.getProperty("thousands_separator");
        if (property == null) {
            throw new IllegalStateException("No thousands separator configuration provided");
        }
        return property;
    }

    public String getDecimalSeparator() {
        String property = this.properties.getProperty("decimal_separator");
        if (property == null) {
            throw new IllegalStateException("No thousands separator configuration provided");
        }
        return property;
    }

    public String getNumberSeparator() {
        String property = this.properties.getProperty("number_separator");
        if (property == null) {
            throw new IllegalStateException("No thousands separator configuration provided");
        }
        return property;
    }
}
