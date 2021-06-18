package io.github.mportilho.numbers2words.options;

public enum N2WZeroDisplay {

    INTEGER_ONLY, FRACTION_ONLY, BOTH, NONE;

    public boolean forInteger() {
        return INTEGER_ONLY.equals(this) || BOTH.equals(this);
    }

    public boolean forFraction() {
        return FRACTION_ONLY.equals(this) || BOTH.equals(this);
    }

    public boolean forBoth() {
        return BOTH.equals(this);
    }
    
}
