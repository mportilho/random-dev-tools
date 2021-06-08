package io.github.mportilho.numbers2words;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Numbers2WordsParser {

    public void parse(Number number) {
        ThousandBlockNumber numberParts = partitionIntoBlocks(number);
        System.out.println(numberParts);
    }

    private ThousandBlockNumber partitionIntoBlocks(Number source) {
        if (source == null) {
            return new ThousandBlockNumber();
        } else if (source instanceof Integer || source instanceof Short || source instanceof Long) {
            BigDecimal wholePart = new BigDecimal(source.longValue());
            return new ThousandBlockNumber(wholePart.signum(), extractBlocks(wholePart), Collections.emptyList());
        } else {
            BigDecimal wholePart = source instanceof BigDecimal ? ((BigDecimal) source) : new BigDecimal(source.toString());
            BigDecimal fractionPart = wholePart.abs().remainder(BigDecimal.ONE);
            wholePart = wholePart.subtract(fractionPart);
            fractionPart = fractionPart.movePointRight(wholePart.scale());
            return new ThousandBlockNumber(wholePart.signum(), extractBlocks(wholePart), extractBlocks(fractionPart));
        }
    }

    private List<BigDecimal> extractBlocks(BigDecimal number) {
        if (number == null) {
            return Collections.emptyList();
        }
        number = number.abs().stripTrailingZeros();
        List<BigDecimal> blocks = new ArrayList<>();
        while (number.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal block = number.divide(new BigDecimal(1000));
            BigDecimal fraction = block.remainder(BigDecimal.ONE);
            blocks.add(fraction.movePointRight(block.scale()).abs());
            number = block.subtract(fraction);
        }
        Collections.reverse(blocks);
        return blocks;
    }

}
