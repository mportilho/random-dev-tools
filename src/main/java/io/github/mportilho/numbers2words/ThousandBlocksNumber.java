package io.github.mportilho.numbers2words;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class ThousandBlocksNumber {

    private final int signal;
    private final BigDecimal integer;
    private final BigDecimal fraction;
    private final int scale;
    private final boolean singularInteger;
    private final boolean singularFraction;
    private final List<Integer> wholeNumberBlocks;
    private final List<Integer> fractionalNumberBlocks;

    public ThousandBlocksNumber() {
        this.signal = 0;
        this.integer = null;
        this.fraction = null;
        this.scale = 0;
        this.singularInteger = true;
        this.singularFraction = true;
        this.wholeNumberBlocks = Collections.emptyList();
        this.fractionalNumberBlocks = Collections.emptyList();
    }

    public ThousandBlocksNumber(BigDecimal integer, BigDecimal fraction, int scale,
                                List<Integer> wholeNumberBlocks, List<Integer> fractionalNumberBlocks) {
        this.signal = integer.signum();
        this.integer = integer;
        this.fraction = fraction;
        this.scale = scale;
        this.singularInteger = integer.compareTo(BigDecimal.ONE) == 0;
        this.singularFraction = fraction.compareTo(BigDecimal.ONE) == 0;
        this.wholeNumberBlocks = wholeNumberBlocks;
        this.fractionalNumberBlocks = fractionalNumberBlocks;
    }

    public int getSignal() {
        return signal;
    }

    public List<Integer> getWholeNumberBlocks() {
        return wholeNumberBlocks;
    }

    public List<Integer> getFractionalNumberBlocks() {
        return fractionalNumberBlocks;
    }

    public boolean isSingularInteger() {
        return singularInteger;
    }

    public boolean isSingularFraction() {
        return singularFraction;
    }

    public BigDecimal getInteger() {
        return integer;
    }

    public BigDecimal getFraction() {
        return fraction;
    }

    public int getScale() {
        return scale;
    }

    @Override
    public String toString() {
        return "ThousandBlocksNumber{" +
                "signal=" + signal +
                ", wholeNumberBlocks=" + wholeNumberBlocks +
                ", fractionalNumberBlocks=" + fractionalNumberBlocks +
                '}';
    }
}
