package io.github.mportilho.numbers2words;

import io.github.mportilho.assertions.Asserts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Numbers2WordsParser {

    private static final String CLASS_NUMBER = "number";
    private static final String CLASS_SCALE = "scale";

    private final Numbers2WordsOptions wordOptions;

    public Numbers2WordsParser(Numbers2WordsOptions wordOptions) {
        this.wordOptions = wordOptions;
    }

    public String parse(Number number) {
        ThousandBlocksNumber numberBlocks = partitionIntoBlocks(number);
        return formatThousandBlocksNumber(numberBlocks);
    }

    private String formatThousandBlocksNumber(ThousandBlocksNumber numberBlocks) {
        StringBuilder builder = new StringBuilder();
        StringBuilder wholeNumberBuilder = formatNumberBlocks(numberBlocks.getWholeNumberBlocks(), 0);
        StringBuilder fractionalNumberBuilder = formatNumberBlocks(numberBlocks.getFractionalNumberBlocks(), numberBlocks.getScale());

        if (wholeNumberBuilder != null) {
            builder.append(wholeNumberBuilder);
            appendIfExists(builder, searchSuffixWord("integer.suffix", numberBlocks.getInteger().intValue()));
        }
        if (fractionalNumberBuilder != null) {
            appendIfExists(builder, getDecimalSeparator());
            builder.append(fractionalNumberBuilder);
            appendIfExists(builder, searchSuffixWord("fraction.suffix", numberBlocks.getFraction().intValue()));
        }
        return builder.toString();
    }

    private StringBuilder formatNumberBlocks(List<Integer> blocks, int fractionScale) {
        int blockQuantity = blocks.size();
        StringBuilder wordBuilder = new StringBuilder();

        for (int i = 0; i < blockQuantity; i++) {
            if (wordBuilder.length() > 0) {
                wordBuilder.append(getThousandsSeparator());
            }
            wordBuilder.append(composeTextualReference(blocks.get(i), "integer"));
            int scale = (blockQuantity - 1 - i) * 3;
            String scaleWord = searchScaleWord("integer", scale);
            if (scale > 0 && scaleWord == null) {
                throw new IllegalStateException(String.format("Scale description for integer scale of %d not found", scale));
            } else if (scaleWord != null) {
                wordBuilder.append(' ').append(scaleWord);
            }
        }
        appendIfExists(wordBuilder, searchScaleWord("fraction", fractionScale));
        return wordBuilder.length() > 0 ? wordBuilder : null;
    }

    private String searchScaleWord(String partName, int scale) {
        if (scale < 0) {
            return null;
        }
        return findPropertyValue(partName, CLASS_SCALE, scale, "");
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
                wordBuilder.append(temp).append((tenthPart != 0 || unitPart != 0) ? getNumberSeparator() : "");
            }
            if (tenthPart != 0 && unitPart != 0) {
                String temp = searchWord(partName, CLASS_NUMBER, tenthPart + unitPart, true);
                if (temp != null) {
                    wordBuilder.append(temp);
                } else {
                    temp = searchWord(partName, CLASS_NUMBER, tenthPart, true);
                    checkExistence(temp, tenthPart, number);
                    wordBuilder.append(temp).append(getNumberSeparator());
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

    protected String searchWord(String partName, String numberClassName, int number, boolean modifierFirst) {
        String numberType = number == 1 ? "singular" : "plural";
        String wordGender = wordOptions.getGender() != null ? wordOptions.getGender().getLabel() : "";
        String value = findPropertyValueWithClassifier(new StringBuilder(partName).append('.').append(numberClassName)
                .append('.').append(number).append(modifierFirst ? "+" : "").toString(), wordGender, numberType);
        if (value == null) {
            return findPropertyValueWithClassifier(new StringBuilder(partName).append('.').append(numberClassName)
                    .append('.').append(number).append(!modifierFirst ? "+" : "").toString(), wordGender, numberType);
        }
        return value;
    }

    protected String searchSuffixWord(String searchPhrase, int number) {
        String numberType = number == 1 ? "singular" : "plural";
        String wordGender = wordOptions.getGender() != null ? wordOptions.getGender().getLabel() : "";
        return findPropertyValueWithClassifier(searchPhrase, wordGender, numberType);
    }

    private String findPropertyValue(String partName, String numberClassName, int number, String searchModifier) {
        String numberType = number == 1 ? "singular" : "plural";
        String wordGender = wordOptions.getGender() != null ? wordOptions.getGender().getLabel() : "";
        return findPropertyValueWithClassifier(new StringBuilder(partName).append('.').append(numberClassName)
                .append('.').append(number).append(searchModifier).toString(), wordGender, numberType);
    }

    private String findPropertyValueWithClassifier(String searchPhrase, String wordGender, String numberType) {
        String value = null;
        if (wordGender != null && !wordGender.isBlank()) {
            value = wordOptions.getProperties().getProperty(new StringBuilder(searchPhrase).append('.').append(wordGender)
                    .append('.').append(numberType).toString());
            if (value == null || value.isBlank()) {
                value = wordOptions.getProperties().getProperty(new StringBuilder(searchPhrase).append('.').append(wordGender)
                        .toString());
            }
        }
        if (value == null || value.isBlank()) {
            value = wordOptions.getProperties().getProperty(new StringBuilder(searchPhrase).append('.').append(numberType).toString());
        }
        if (value == null || value.isBlank()) {
            value = wordOptions.getProperties().getProperty(new StringBuilder(searchPhrase).toString());
        }
        return value;
    }


    private ThousandBlocksNumber partitionIntoBlocks(Number source) {
        if (source == null) {
            return new ThousandBlocksNumber();
        } else if (source instanceof Integer || source instanceof Short || source instanceof Long) {
            BigDecimal integerPart = new BigDecimal(source.intValue());
            return new ThousandBlocksNumber(integerPart, BigDecimal.ZERO, 0, extractBlocks(integerPart), Collections.emptyList());
        } else {
            BigDecimal integerPart = source instanceof BigDecimal ? ((BigDecimal) source) : new BigDecimal(source.toString());
            BigDecimal fractionPart = integerPart.abs().remainder(BigDecimal.ONE);
            integerPart = integerPart.subtract(fractionPart);
            fractionPart = fractionPart.movePointRight(integerPart.scale());
            return new ThousandBlocksNumber(integerPart, fractionPart, integerPart.scale(), extractBlocks(integerPart), extractBlocks(fractionPart));
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

    private void appendIfExists(StringBuilder builder, CharSequence value) {
        if (value != null && value.length() > 0) {
            builder.append(' ').append(value);
        }
    }

}
