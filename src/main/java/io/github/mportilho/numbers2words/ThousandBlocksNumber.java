package io.github.mportilho.numbers2words;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class ThousandBlocksNumber {

    private final int signal;
    private final List<Long> wholeNumberBlocks;
    private final List<Long> fractionalNumberBlocks;

    public ThousandBlocksNumber() {
        this.signal = 1;
        this.wholeNumberBlocks = Collections.emptyList();
        this.fractionalNumberBlocks = Collections.emptyList();
    }

    public ThousandBlocksNumber(int signal, List<Long> wholeNumberBlocks, List<Long> fractionalNumberBlocks) {
        this.signal = signal;
        this.wholeNumberBlocks = wholeNumberBlocks;
        this.fractionalNumberBlocks = fractionalNumberBlocks;
    }

    public int getSignal() {
        return signal;
    }

    public List<Long> getWholeNumberBlocks() {
        return wholeNumberBlocks;
    }

    public List<Long> getFractionalNumberBlocks() {
        return fractionalNumberBlocks;
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
