package io.github.mportilho.numbers2words;

import io.github.mportilho.assertions.Asserts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Numbers2WordsParser {

    private static final String CLASS_NUMBER = "number";
    private static final String CLASS_SCALE = "scale";

    private Numbers2WordsOptions wordOptions;

    public Numbers2WordsParser(Numbers2WordsOptions wordOptions) {
        this.wordOptions = wordOptions;
    }

    public void parse(Number number) {
        ThousandBlocksNumber numberBlocks = partitionIntoBlocks(number);
        formatThousandBlocksNumber(numberBlocks);
        System.out.println(numberBlocks);
    }

    private String formatThousandBlocksNumber(ThousandBlocksNumber numberBlocks) {
        StringBuilder builder = new StringBuilder();

        StringBuilder wholeNumberBuilder = formatNumberBlocks("whole", numberBlocks.getWholeNumberBlocks());
        StringBuilder fractionalNumberBuilder = formatNumberBlocks("whole", numberBlocks.getFractionalNumberBlocks());

        builder.append(wholeNumberBuilder);
//        if (wordOptions.getProperties().getProperty(""))
        builder.append(" reais ");

        if (fractionalNumberBuilder.length() > 0) {
            builder.append(getDecimalSeparator());
            builder.append(fractionalNumberBuilder);
            builder.append(" centavos ");
            int fractionBlockSize = numberBlocks.getFractionalNumberBlocks().size();
            int pos = 0;
            if (fractionBlockSize > 0) {
                int number = numberBlocks.getFractionalNumberBlocks().get(fractionBlockSize - 1);
                int hundredPart = (number / 100) * 100;
                int tenthPart = ((number - hundredPart) / 10) * 10;
                int unitPart = number - hundredPart - tenthPart;
                pos += (hundredPart > 0 ? 1 : 0) + (tenthPart > 0 ? 1 : 0) + (unitPart > 0 ? 1 : 0);
            }
            if (fractionBlockSize > 1) {
                pos += (fractionBlockSize - 1) * 3;
            }
            builder.append(searchScaleWord("fraction", pos, false));
        }
        return builder.toString();
    }

    private StringBuilder formatNumberBlocks(String partName, List<Integer> blocks) {
        int blockQuantity = blocks.size();
        StringBuilder wordBuilder = new StringBuilder();

        for (int i = 0; i < blockQuantity; i++) {
            if (wordBuilder.length() > 0) {
                wordBuilder.append(" e ");
            }
            wordBuilder.append(composeTextualReference(blocks.get(i), partName));
            int scale = (blockQuantity - 1 - i) * 3;
            String scaleWord = searchScaleWord(partName, scale, false);
            if (scale > 0 && scaleWord == null) {
                throw new IllegalStateException(String.format("Scale description for integer scale of %d not found", scale));
            } else if (scaleWord != null) {
                wordBuilder.append(' ').append(scaleWord);
            }
        }
        return wordBuilder;
    }

    private String searchScaleWord(String partName, int scale, boolean modifierFirst) {
        if (scale < 0) {
            return null;
        }
        String value = findPropertyValue(partName, CLASS_SCALE, scale, modifierFirst ? "+" : "");
        if (value != null) {
            return value;
        }
        return findPropertyValue(partName, CLASS_SCALE, scale, !modifierFirst ? "+" : "");
    }

    private StringBuilder composeTextualReference(int number, String partName) {
        StringBuilder wordBuilder = new StringBuilder();
        int hundredPart = (number / 100) * 100;
        int tenthPart = ((number - hundredPart) / 10) * 10;
        int unitPart = number - hundredPart - tenthPart;

        String word = searchWord(partName, CLASS_NUMBER, number, false);
        if (word != null) {
            wordBuilder.append(word);
        } else {
            if (hundredPart != 0) {
                String temp = searchWord(partName, CLASS_NUMBER, hundredPart, true);
                checkExistence(temp, hundredPart, number);
                wordBuilder.append(temp).append((tenthPart != 0 || unitPart != 0) ? " e " : "");
            }
            if (tenthPart != 0 && unitPart != 0) {
                String temp = searchWord(partName, CLASS_NUMBER, tenthPart + unitPart, true);
                if (temp != null) {
                    wordBuilder.append(temp);
                } else {
                    temp = searchWord(partName, CLASS_NUMBER, tenthPart, true);
                    checkExistence(temp, tenthPart, number);
                    wordBuilder.append(temp).append(" e ");
                    temp = searchWord(partName, CLASS_NUMBER, unitPart, true);
                    checkExistence(temp, tenthPart, number);
                    wordBuilder.append(temp);
                }
            }
            if (tenthPart == 0 && unitPart != 0) {
                String temp = searchWord(partName, CLASS_NUMBER, unitPart, true);
                checkExistence(temp, tenthPart, number);
                wordBuilder.append(temp);
            }
        }
        return wordBuilder;
    }

    private String searchWord(String partName, String numberClass, int number, boolean modifierFirst) {
        String value = findPropertyValue(partName, numberClass, number, modifierFirst ? "+" : "");
        if (value != null) {
            return value;
        }
        return findPropertyValue(partName, numberClass, number, !modifierFirst ? "+" : "");
    }

    private String findPropertyValue(String partName, String numberClassName, int number, String searchModifier) {
        String numberType = number == 1l ? "singular" : "plural";
        String wordGender = wordOptions.getGender() != null ? wordOptions.getGender().getLabel() : "";
        String value;

        value = wordOptions.getProperties().getProperty(new StringBuilder(partName).append('.').append(numberClassName)
                .append('.').append(number).append(searchModifier).append('.').append(wordGender).append('.')
                .append(numberType).toString());
        if (value == null || value.isBlank()) {
            value = wordOptions.getProperties().getProperty(new StringBuilder(partName).append('.').append(numberClassName)
                    .append('.').append(number).append(searchModifier).append('.').append(numberType).toString());
        }
        if (value == null || value.isBlank()) {
            value = wordOptions.getProperties().getProperty(new StringBuilder(partName).append('.').append(numberClassName).append('.')
                    .append(number).append(searchModifier).toString());
        }
        return value;
    }


    private ThousandBlocksNumber partitionIntoBlocks(Number source) {
        if (source == null) {
            return new ThousandBlocksNumber();
        } else if (source instanceof Integer || source instanceof Short || source instanceof Long) {
            BigDecimal wholePart = new BigDecimal(source.intValue());
            return new ThousandBlocksNumber(wholePart.signum(), extractBlocks(wholePart), Collections.emptyList());
        } else {
            BigDecimal wholePart = source instanceof BigDecimal ? ((BigDecimal) source) : new BigDecimal(source.toString());
            BigDecimal fractionPart = wholePart.abs().remainder(BigDecimal.ONE);
            wholePart = wholePart.subtract(fractionPart);
            fractionPart = fractionPart.movePointRight(wholePart.scale());
            return new ThousandBlocksNumber(wholePart.signum(), extractBlocks(wholePart), extractBlocks(fractionPart));
        }
    }

    private List<Integer> extractBlocks(BigDecimal number) {
        if (number == null) {
            return Collections.emptyList();
        }
        if (number.compareTo(BigDecimal.ZERO) == 0) {
            return Collections.singletonList(0);
        }
        number = number.abs().stripTrailingZeros();
        List<Integer> blocks = new ArrayList<>();
        while (number.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal block = number.divide(new BigDecimal(1000));
            BigDecimal fraction = block.remainder(BigDecimal.ONE);
            blocks.add(fraction.movePointRight(block.scale()).abs().intValue());
            number = block.subtract(fraction);
        }
        Collections.reverse(blocks);
        return blocks;
    }
    
    private String getThousandsSeparator() {
        String property = wordOptions.getProperties().getProperty("thousands_separator");
        if (property == null) {
            throw new IllegalStateException("No thousands separator configuration provided");
        }
        return property;
    }

    private String getDecimalSeparator() {
        String property = wordOptions.getProperties().getProperty("decimal_separator");
        if (property == null) {
            throw new IllegalStateException("No thousands separator configuration provided");
        }
        return property;
    }

    private String getNumberSeparator() {
        String property = wordOptions.getProperties().getProperty("number_separator");
        if (property == null) {
            throw new IllegalStateException("No thousands separator configuration provided");
        }
        return property;
    }


    private void checkExistence(String value, int number, int part) {
        if (Asserts.isEmpty(value)) {
            throw new IllegalStateException(String.format("No word mapped for %s from number %d", part, number));
        }
    }

}
