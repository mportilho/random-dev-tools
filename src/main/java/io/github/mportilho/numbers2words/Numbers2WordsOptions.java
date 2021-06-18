package io.github.mportilho.numbers2words;

import io.github.mportilho.numbers2words.i18n.ThousandSeparatorRule;
import io.github.mportilho.numbers2words.options.*;

import java.util.Properties;

public class Numbers2WordsOptions {

    private final Properties properties;
    private final WordGender gender;
    private final ThousandSeparatorRule rule;

    private final N2WUnitDisplay unitDisplay;
    private final N2WScaleDisplay scaleDisplay;
    private final N2WZeroDisplay zeroDisplay;
    private final N2WSingularWordDisplay singularWordDisplay;
    private final N2WAppendSingularUnitToZero appendSingularUnitToZero;

    protected Numbers2WordsOptions(Properties properties, WordGender gender, ThousandSeparatorRule rule, N2WUnitDisplay unitDisplay,
                                   N2WScaleDisplay scaleDisplay, N2WZeroDisplay zeroDisplay,
                                   N2WSingularWordDisplay singularWordDisplay, N2WAppendSingularUnitToZero appendSingularUnitToZero) {
        this.properties = properties;
        this.gender = gender;
        this.rule = rule;
        this.unitDisplay = unitDisplay;
        this.scaleDisplay = scaleDisplay;
        this.zeroDisplay = zeroDisplay;
        this.singularWordDisplay = singularWordDisplay;
        this.appendSingularUnitToZero = appendSingularUnitToZero;
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
