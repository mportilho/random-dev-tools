package io.github.mportilho.numbers2words;

import io.github.mportilho.assertions.Asserts;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Numbers2WordsParser {

    public static final BigDecimal A_THOUSAND = new BigDecimal(1000);
    private final Numbers2WordsOptions wordOptions;

    public Numbers2WordsParser(Numbers2WordsOptions wordOptions) {
        this.wordOptions = wordOptions;
    }

    public String parse(Number number) {
        ThousandBlocksNumber numberBlocks = partitionIntoBlocks(number);
        return createTextualRepresentation(numberBlocks);
    }

    private ThousandBlocksNumber partitionIntoBlocks(Number source) {
        if (source == null) {
            return new ThousandBlocksNumber();
        } else if (source instanceof Integer || source instanceof Short || source instanceof Long) {
            BigDecimal integerPart = new BigDecimal(source.longValue());
            return new ThousandBlocksNumber(integerPart, BigDecimal.ZERO, 0, extractBlocks(integerPart), Collections.emptyList());
        } else {
            BigDecimal originalNumber = (source instanceof BigDecimal ? ((BigDecimal) source) : new BigDecimal(source.toString())).stripTrailingZeros();
            BigDecimal integerPart = originalNumber;
            BigDecimal fractionPart = integerPart.abs().remainder(BigDecimal.ONE);
            integerPart = integerPart.subtract(fractionPart);
            fractionPart = fractionPart.movePointRight(integerPart.scale());
            List<Integer> fractionNumberBlocks = fractionPart.compareTo(BigDecimal.ZERO) > 0 ? extractBlocks(fractionPart) : Collections.emptyList();
            return new ThousandBlocksNumber(integerPart, fractionPart, originalNumber.scale(), extractBlocks(integerPart), fractionNumberBlocks);
        }
    }

    protected static List<Integer> extractBlocks(BigDecimal number) {
        if (number == null) {
            return Collections.emptyList();
        }
        if (number.compareTo(BigDecimal.ZERO) == 0) {
            return Collections.singletonList(0);
        }
        number = number.abs().stripTrailingZeros();
        List<Integer> blocks = new ArrayList<>();
        while (number.compareTo(BigDecimal.ZERO) > 0) {
            if (number.compareTo(A_THOUSAND) == 0) {
                blocks.add(0);
                blocks.add(1);
                number = BigDecimal.ZERO;
            } else if (number.compareTo(A_THOUSAND) < 0 && number.scale() == 0) {
                blocks.add(number.intValue());
                number = BigDecimal.ZERO;
            } else if (number.intValue() == 0 && number.scale() != 0) {
                if (number.unscaledValue().compareTo(BigInteger.ONE) == 0) {
                    blocks.add(1);
                } else {
                    blocks = extractBlocks(new BigDecimal(number.unscaledValue()));
                }
                number = BigDecimal.ZERO;
            } else {
                BigDecimal temp = number.movePointLeft(3);
                BigDecimal fraction = temp.remainder(BigDecimal.ONE);
                number = temp.subtract(fraction).stripTrailingZeros();
                blocks.add(fraction.movePointRight(3).intValue());
            }
        }
        Collections.reverse(blocks);
        return blocks;
    }

    private String createTextualRepresentation(ThousandBlocksNumber numberBlocks) {
        StringBuilder builder = new StringBuilder();
        StringBuilder integerNumberBuilder = formatNumericBlocks(numberBlocks.getIntegerNumberBlocks(), true);
        StringBuilder fractionalNumberBuilder = formatNumericBlocks(numberBlocks.getFractionalNumberBlocks(), false);

        boolean isAddingIntegerPart = integerNumberBuilder != null &&
                (!numberBlocks.isZeroInteger() || (numberBlocks.isZeroInteger() && wordOptions.getZeroDisplay().forInteger()));

        if (isAddingIntegerPart) {
            builder.append(integerNumberBuilder);
            if (wordOptions.getUnitDisplay().forInteger() ||
                    wordOptions.getUnitDisplay().forInteger() && numberBlocks.isZeroInteger() && wordOptions.getAppendSingularUnitToZero().forInteger()) {
                appendIfExists(builder, searchSuffixWord("integer.suffix", numberBlocks.getInteger().intValue()), true);
            }
        }
        if (fractionalNumberBuilder != null) {
            if (isAddingIntegerPart) {
                appendIfExists(builder, wordOptions.getDecimalSeparator(), false);
            }
            builder.append(fractionalNumberBuilder);
            if (wordOptions.getScaleDisplay().forFraction()) {
                String scaleSuffix = numberBlocks.getScale() < 0 ? null :
                        searchSuffixWord("fraction.scale." + numberBlocks.getScale(), numberBlocks.getFraction().intValue());
                appendIfExists(builder, scaleSuffix, true);
            }
            if (wordOptions.getUnitDisplay().forFraction()) {
                appendIfExists(builder, searchSuffixWord("fraction.suffix", numberBlocks.getFraction().intValue()), true);
            }
        }
        return builder.toString();
    }

    private StringBuilder formatNumericBlocks(List<Integer> blocks, boolean isIntegerPart) {
        int blockQuantity = blocks.size();
        StringBuilder wordBuilder = new StringBuilder();
        int prevNumber = Integer.MIN_VALUE;
        int prevScale = Integer.MIN_VALUE;

        for (int i = 0; i < blockQuantity; i++) {
            int currNumber = blocks.get(i);
            int currScale = (blockQuantity - 1 - i) * 3;
            boolean addNextBlock = currNumber != 0 || (isIntegerPart && wordOptions.getZeroDisplay().forInteger()) ||
                    (!isIntegerPart && wordOptions.getZeroDisplay().forFraction());
            addNextBlock = addNextBlock && (currNumber != 1 || (isIntegerPart && wordOptions.getSingularWordDisplay().forInteger()) ||
                    (!isIntegerPart && wordOptions.getSingularWordDisplay().forFraction()));

            if (addNextBlock) {
                boolean lastBlock = true;
                for (int j = i + 1; j < blockQuantity; j++) {
                    lastBlock = lastBlock && blocks.get(j) == 0;
                }
                lastBlock = lastBlock || i == blockQuantity - 1;

                if (wordOptions.getRule().useThousandSeparator(prevScale, currScale, prevNumber, currNumber, i, blockQuantity,
                        wordBuilder.length() > 0, lastBlock)) {
                    wordBuilder.append(wordOptions.getThousandsSeparator());
                } else if (wordBuilder.length() > 0) {
                    wordBuilder.append(' ');
                }

                StringBuilder text = composeNumericBlockText(currNumber);
                String scaleWord = currScale >= 0 ? searchSuffixWord("integer.scale." + currScale, currNumber) : null;
                if (currScale > 0 && scaleWord == null) {
                    throw new IllegalStateException(String.format("Scale description for integer scale of %d not found", currScale));
                }
                wordOptions.getRule().composeTextRepresentation(wordBuilder, currNumber, currScale, text, scaleWord, i, blockQuantity);
            }
            prevNumber = currNumber;
            prevScale = currScale;
        }
        return wordBuilder.length() > 0 ? wordBuilder : null;
    }

    private StringBuilder composeNumericBlockText(int number) {
        StringBuilder wordBuilder = new StringBuilder();
        int hundredPart = (number / 100) * 100;
        int tenthPart = ((number - hundredPart) / 10) * 10;
        int unitPart = number - hundredPart - tenthPart;

        String word = searchNumberRepresentation(number, false);
        if (word != null) {
            wordBuilder.append(word);
        } else {
            if (hundredPart != 0) {
                String temp = searchNumberRepresentation(hundredPart, true);
                checkExistence(temp, hundredPart, number);
                wordBuilder.append(temp).append((tenthPart != 0 || unitPart != 0) ? wordOptions.getNumberSeparator() : "");
            }
            if (tenthPart != 0 && unitPart != 0) {
                String temp = searchNumberRepresentation(tenthPart + unitPart, true);
                if (temp != null) {
                    wordBuilder.append(temp);
                } else {
                    temp = searchNumberRepresentation(tenthPart, true);
                    checkExistence(temp, tenthPart, number);
                    wordBuilder.append(temp).append(wordOptions.getNumberSeparator());
                    temp = searchNumberRepresentation(unitPart, true);
                    checkExistence(temp, unitPart, number);
                    wordBuilder.append(temp);
                }
            } else if (tenthPart != 0) {
                String temp = searchNumberRepresentation(tenthPart, true);
                checkExistence(temp, tenthPart, number);
                wordBuilder.append(temp);
            } else {
                if (unitPart != 1 || wordOptions.getSingularWordDisplay().forInteger()) {
                    String temp = searchNumberRepresentation(unitPart, true);
                    checkExistence(temp, unitPart, number);
                    wordBuilder.append(temp);
                }
            }
        }
        return wordBuilder;
    }

    protected String searchNumberRepresentation(int number, boolean modifierFirst) {
        WordCountType numberType = number == 1 ? WordCountType.SINGULAR : WordCountType.PLURAL;
        String wordGender = wordOptions.getGender() != null ? wordOptions.getGender().getLabel() : "";
        String searchPhrase = new StringBuilder("integer.number.").append(number).append(modifierFirst ? "+" : "").toString();
        String value = findPropertyValueWithClassifier(searchPhrase, wordGender, numberType.getLabel());
        if (value == null) {
            searchPhrase = new StringBuilder("integer.number.").append(number).append(!modifierFirst ? "+" : "").toString();
            return findPropertyValueWithClassifier(searchPhrase, wordGender, numberType.getLabel());
        }
        return value;
    }

    protected String searchSuffixWord(String searchPhrase, int number) {
        WordCountType numberType = number == 1 ? WordCountType.SINGULAR : WordCountType.PLURAL;
        String wordGender = wordOptions.getGender() != null ? wordOptions.getGender().getLabel() : "";
        return findPropertyValueWithClassifier(searchPhrase, wordGender, numberType.getLabel());
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

    private void checkExistence(String value, int number, int part) {
        if (Asserts.isEmpty(value)) {
            throw new IllegalStateException(String.format("No word mapped for %s from number %d", part, number));
        }
    }

    private void appendIfExists(StringBuilder builder, CharSequence value, boolean addSpacePrefixChar) {
        if (value != null && value.length() > 0) {
            if (addSpacePrefixChar) {
                builder.append(' ');
            }
            builder.append(value);
        }
    }

}
