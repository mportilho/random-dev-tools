package io.github.mportilho.numbers2words;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Numbers2WordsParser {

    private Numbers2WordsOptions wordOptions;
    private Properties properties;

    public Numbers2WordsParser() {
        this.properties = new Properties();
    }

    public Numbers2WordsParser(Numbers2WordsOptions wordOptions) {
        this.wordOptions = wordOptions;
        this.properties = new Properties();
    }

    public void parse(Number number) {
        ThousandBlocksNumber numberBlocks = partitionIntoBlocks(number);
        formatThousandBlocksNumber(numberBlocks);
        System.out.println(numberBlocks);
    }

    private void formatThousandBlocksNumber(ThousandBlocksNumber numberBlocks) {
        StringBuilder builder = new StringBuilder();
        formatNumberBlocks(builder, numberBlocks.getSignal(), "whole", numberBlocks.getWholeNumberBlocks());
        formatNumberBlocks(builder, numberBlocks.getSignal(), "fractional", numberBlocks.getWholeNumberBlocks());
    }

    private void formatNumberBlocks(StringBuilder builder, int signal, String partName, List<Long> blocks) {
//        whole.unitary.1.feminine.singular=uma
//        whole.mil.3.singular=mil
//        whole.unitary.1=um
//        fraction.mil.1.singular=décimo
//        whole.unitary.200+=duzentos
        int blockQuantity = blocks.size();
        for (int i = 0; i < blockQuantity; i++) {
            long number = blocks.get(i);
            long hundredPart = (number / 100) * 100;
            long tenthPart = (number / 10) * 10;
            long unitPart = number - hundredPart - tenthPart;
            String wordGender = wordOptions.getGender() != null ? wordOptions.getGender().getLabel() : "";

            StringBuilder key = new StringBuilder(partName).append('.').append("unitary");
            properties.getProperty(key.toString());
        }
    }

    private String findPropertyValue(String partName, String numberClassName, long number, Character searchModifier, String wordGender) {
        String numberType = number == 1l ? "singular" : "plural";
        String value;

        value = properties.getProperty(new StringBuilder(partName).append('.').append(numberClassName).append('.')
                .append(number).append(searchModifier).append('.').append(wordGender).append('.').append(numberType).toString());
        if (value == null || value.isBlank()) {
            value = properties.getProperty(new StringBuilder(partName).append('.').append(numberClassName).append('.')
                    .append(number).append(searchModifier).append('.').append(numberType).toString());
        }
        if (value == null || value.isBlank()) {
            value = properties.getProperty(new StringBuilder(partName).append('.').append(numberClassName).append('.')
                    .append(number).append(searchModifier).toString());
        }
        return value;
    }


    private ThousandBlocksNumber partitionIntoBlocks(Number source) {
        if (source == null) {
            return new ThousandBlocksNumber();
        } else if (source instanceof Integer || source instanceof Short || source instanceof Long) {
            BigDecimal wholePart = new BigDecimal(source.longValue());
            return new ThousandBlocksNumber(wholePart.signum(), extractBlocks(wholePart), Collections.emptyList());
        } else {
            BigDecimal wholePart = source instanceof BigDecimal ? ((BigDecimal) source) : new BigDecimal(source.toString());
            BigDecimal fractionPart = wholePart.abs().remainder(BigDecimal.ONE);
            wholePart = wholePart.subtract(fractionPart);
            fractionPart = fractionPart.movePointRight(wholePart.scale());
            return new ThousandBlocksNumber(wholePart.signum(), extractBlocks(wholePart), extractBlocks(fractionPart));
        }
    }

    private List<Long> extractBlocks(BigDecimal number) {
        if (number == null) {
            return Collections.emptyList();
        }
        number = number.abs().stripTrailingZeros();
        List<Long> blocks = new ArrayList<>();
        while (number.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal block = number.divide(new BigDecimal(1000));
            BigDecimal fraction = block.remainder(BigDecimal.ONE);
            blocks.add(fraction.movePointRight(block.scale()).abs().longValue());
            number = block.subtract(fraction);
        }
        Collections.reverse(blocks);
        return blocks;
    }

}
