package io.github.mportilho.numbers2words.i18n;

import java.util.Arrays;
import java.util.List;

public class ThousandSeparatorRule_PT_BR implements ThousandSeparatorRule {

    private static final List<Integer> HUNDREDS = Arrays.asList(100, 200, 300, 400, 500, 600, 700, 800, 900);

    @Override
    public boolean useThousandSeparator(int prevScalar, int currScalar, int prevNumber, int currNumber,
                                        int currIndex, int totalNumberBlocks, boolean textPreviouslyFound, boolean lastBlock) {
        return textPreviouslyFound && lastBlock && (HUNDREDS.contains(currNumber) || currNumber < 100);
    }

    @Override
    public void composeTextRepresentation(StringBuilder builder, int number, int scale, CharSequence numberWord,
                                          CharSequence scaleWord, int currIndex, int totalNumberBlocks) {
        if (number != 1 || currIndex != 0 || scale == 0) {
            if (numberWord != null && numberWord.length() > 0) {
                builder.append(numberWord);
                if (scaleWord != null && scaleWord.length() > 0) {
                    builder.append(' ');
                }
            }
        }
        if (scaleWord != null) {
            builder.append(scaleWord);
        }
    }

}
