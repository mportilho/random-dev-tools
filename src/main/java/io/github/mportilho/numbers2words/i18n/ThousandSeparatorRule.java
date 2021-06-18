package io.github.mportilho.numbers2words.i18n;

public interface ThousandSeparatorRule {

    public boolean useThousandSeparator(int prevScalar, int currScalar, int prevNumber, int currNumber, 
                                        int currIndex, int totalNumberBlocks, boolean textPreviouslyFound);

}
