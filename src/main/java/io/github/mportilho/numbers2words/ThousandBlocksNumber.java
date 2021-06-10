package io.github.mportilho.numbers2words;

import java.util.Collections;
import java.util.List;

public class ThousandBlocksNumber {

    private final int signal;
    private final List<Integer> wholeNumberBlocks;
    private final List<Integer> fractionalNumberBlocks;

    public ThousandBlocksNumber() {
        this.signal = 1;
        this.wholeNumberBlocks = Collections.emptyList();
        this.fractionalNumberBlocks = Collections.emptyList();
    }

    public ThousandBlocksNumber(int signal, List<Integer> wholeNumberBlocks, List<Integer> fractionalNumberBlocks) {
        this.signal = signal;
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

    @Override
    public String toString() {
        return "ThousandBlocksNumber{" +
                "signal=" + signal +
                ", wholeNumberBlocks=" + wholeNumberBlocks +
                ", fractionalNumberBlocks=" + fractionalNumberBlocks +
                '}';
    }
}
