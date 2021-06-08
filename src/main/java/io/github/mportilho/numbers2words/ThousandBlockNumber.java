package io.github.mportilho.numbers2words;

import java.math.BigDecimal;
import java.util.List;

public class ThousandBlockNumber {

    private final int signal;
    private final List<BigDecimal> wholeNumberBlocks;
    private final List<BigDecimal> fractionalNumberBlocks;

    public ThousandBlockNumber(int signal, List<BigDecimal> wholeNumberBlocks, List<BigDecimal> fractionalNumberBlocks) {
        this.signal = signal;
        this.wholeNumberBlocks = wholeNumberBlocks;
        this.fractionalNumberBlocks = fractionalNumberBlocks;
    }

    public int getSignal() {
        return signal;
    }

    public List<BigDecimal> getWholeNumberBlocks() {
        return wholeNumberBlocks;
    }

    public List<BigDecimal> getFractionalNumberBlocks() {
        return fractionalNumberBlocks;
    }

    @Override
    public String toString() {
        return "ThousandBlockNumber{" +
                "signal=" + signal +
                ", wholeNumberBlocks=" + wholeNumberBlocks +
                ", fractionalNumberBlocks=" + fractionalNumberBlocks +
                '}';
    }
}
