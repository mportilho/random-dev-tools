package io.github.mportilho.numbers2words;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class ThousandBlocksNumber {

    private final int signal;
    private final BigDecimal integer;
    private final BigDecimal fraction;
    private final int scale;
    private final List<Integer> integerNumberBlocks;
    private final List<Integer> fractionalNumberBlocks;

    public ThousandBlocksNumber() {
        this.signal = 0;
        this.integer = null;
        this.fraction = null;
        this.scale = 0;
        this.integerNumberBlocks = Collections.emptyList();
        this.fractionalNumberBlocks = Collections.emptyList();
    }

    public ThousandBlocksNumber(BigDecimal integer, BigDecimal fraction, int scale,
                                List<Integer> integerNumberBlocks, List<Integer> fractionalNumberBlocks) {
        this.signal = integer.signum();
        this.integer = integer;
        this.fraction = fraction;
        this.scale = scale;
        this.integerNumberBlocks = integerNumberBlocks;
        this.fractionalNumberBlocks = fractionalNumberBlocks;
    }

    public int getSignal() {
        return signal;
    }

    public List<Integer> getIntegerNumberBlocks() {
        return integerNumberBlocks;
    }

    public List<Integer> getFractionalNumberBlocks() {
        return fractionalNumberBlocks;
    }

    public BigDecimal getInteger() {
        return integer;
    }

    public BigDecimal getFraction() {
        return fraction;
    }

    public boolean isZeroInteger() {
        return this.integer.compareTo(BigDecimal.ZERO) == 0;
    }

    public int getScale() {
        return scale;
    }

    @Override
    public String toString() {
        return "ThousandBlocksNumber{" +
                "signal=" + signal +
                ", wholeNumberBlocks=" + integerNumberBlocks +
                ", fractionalNumberBlocks=" + fractionalNumberBlocks +
                '}';
    }
}
