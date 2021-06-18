package io.github.mportilho.numbers2words.i18n;

import java.util.Arrays;
import java.util.List;

public class ThousandSeparatorRule_PT_BR implements ThousandSeparatorRule {

    private static final List<Integer> HUNDREDS = Arrays.asList(100, 200, 300, 400, 500, 600, 700, 800, 900);

    @Override
    public boolean useThousandSeparator(int prevScalar, int currScalar, int prevNumber, int currNumber,
                                        int currIndex, int totalNumberBlocks, boolean textPreviouslyFound) {
        boolean lastElement = currIndex == totalNumberBlocks - 1;
        return textPreviouslyFound && lastElement && (HUNDREDS.contains(currNumber) || currNumber < 100);
    }

}
