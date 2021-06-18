package io.github.mportilho.numbers2words;

import io.github.mportilho.numbers2words.i18n.ThousandSeparatorRule;
import io.github.mportilho.numbers2words.options.*;

import java.util.Objects;
import java.util.Properties;

public final class N2WOptionsBuilder {

    private N2WUnitDisplay unitDisplay;
    private N2WScaleDisplay scaleDisplay;
    private N2WZeroDisplay zeroDisplay;
    private N2WSingularWordDisplay singularWordDisplay;
    private N2WAppendSingularUnitToZero appendSingularUnitToZero;

    private WordGender gender;
    private ThousandSeparatorRule rule;
    private Properties properties;

    private N2WOptionsBuilder() {

    }

    public static N2WOptionsBuilder create(WordGender gender, Properties properties, ThousandSeparatorRule rule) {
        N2WOptionsBuilder builder = new N2WOptionsBuilder();
        builder.properties = Objects.requireNonNull(properties, "Properties cannot be null");
        builder.gender = gender;
        builder.rule = rule;
        builder.unitDisplay = N2WUnitDisplay.BOTH;
        builder.scaleDisplay = N2WScaleDisplay.BOTH;
        builder.zeroDisplay = N2WZeroDisplay.BOTH;
        builder.singularWordDisplay = N2WSingularWordDisplay.BOTH;
        builder.appendSingularUnitToZero = N2WAppendSingularUnitToZero.BOTH;
        return builder;
    }

    public Numbers2WordsOptions build() {
        return new Numbers2WordsOptions(properties, gender, rule, unitDisplay, scaleDisplay, zeroDisplay,
                singularWordDisplay, appendSingularUnitToZero);
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

    public N2WOptionsBuilder unitDisplay(N2WUnitDisplay unitDisplay) {
        this.unitDisplay = unitDisplay;
        return this;
    }

    public N2WOptionsBuilder scaleDisplay(N2WScaleDisplay scaleDisplay) {
        this.scaleDisplay = scaleDisplay;
        return this;
    }

    public N2WOptionsBuilder zeroDisplay(N2WZeroDisplay zeroDisplay) {
        this.zeroDisplay = zeroDisplay;
        return this;
    }

    public N2WOptionsBuilder singularWordDisplay(N2WSingularWordDisplay singularWordDisplay) {
        this.singularWordDisplay = singularWordDisplay;
        return this;
    }

    public N2WOptionsBuilder appendSingularUnitToZero(N2WAppendSingularUnitToZero appendSingularUnitToZero) {
        this.appendSingularUnitToZero = appendSingularUnitToZero;
        return this;
    }

    public N2WUnitDisplay getUnitDisplay() {
        return unitDisplay;
    }

    public N2WScaleDisplay getScaleDisplay() {
        return scaleDisplay;
    }

    public N2WZeroDisplay getZeroDisplay() {
        return zeroDisplay;
    }

    public N2WSingularWordDisplay getSingularWordDisplay() {
        return singularWordDisplay;
    }

    public N2WAppendSingularUnitToZero getAppendSingularUnitToZero() {
        return appendSingularUnitToZero;
    }

    public WordGender getGender() {
        return gender;
    }

    public ThousandSeparatorRule getRule() {
        return rule;
    }
}
