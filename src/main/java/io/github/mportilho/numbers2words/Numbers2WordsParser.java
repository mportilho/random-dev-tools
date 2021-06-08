package io.github.mportilho.numbers2words;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Numbers2WordsParser {

    private DecimalFormatSymbols formatSymbols;

    public Numbers2WordsParser() {
        this.formatSymbols = new DecimalFormatSymbols();
        this.formatSymbols.setGroupingSeparator(',');
        this.formatSymbols.setDecimalSeparator('.');
    }

    public void parse(Number number) {
        ThousandBlockNumber numberParts = partitionNumberIntoParts(number);
        System.out.println(numberParts);
    }

    private ThousandBlockNumber partitionNumberIntoParts(Number source) {
        if (source == null) {
            return null;
        }

        BigDecimal wholePart;
        BigDecimal fractionPart;
        if (source instanceof Integer || source instanceof Short || source instanceof Long) {
            wholePart = new BigDecimal(source.longValue());
            fractionPart = null;
        } else {
            wholePart = source instanceof BigDecimal ? ((BigDecimal) source) : new BigDecimal(source.toString());
            fractionPart = wholePart.abs().remainder(BigDecimal.ONE);
            wholePart = wholePart.subtract(fractionPart);
            fractionPart = fractionPart.movePointRight(wholePart.scale());
        }
        return new ThousandBlockNumber(wholePart.signum(), extractBlocks(wholePart), extractBlocks(fractionPart));
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
