package io.github.mportilho.numbers2words;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public class TestPropertiesSearch {

    @Test
    public void testWordSearchFromNumbers() {
        Properties properties = new Properties();
        properties.put("integer.number.1", "1");
        properties.put("integer.number.1.feminine.singular", "2");
        properties.put("integer.number.2.feminine.plural", "3");
        properties.put("integer.number.1.masculine.singular", "4");
        properties.put("integer.number.2.masculine.plural", "5");
        properties.put("integer.number.1.singular", "6");
        properties.put("integer.number.2.plural", "7");
        properties.put("integer.number.3.feminine", "8");
        properties.put("integer.number.3.masculine", "9");
        properties.put("integer.number.3", "10");
        properties.put("integer.number.4+", "11");
        properties.put("integer.number.4", "12");
        Numbers2WordsOptions options;
        Numbers2WordsParser parser;

        parser = new Numbers2WordsParser(N2WOptionsBuilder.create(WordGender.FEMININE, properties).build());
        Assertions.assertThat(parser.searchNumberRepresentation(1, false)).isEqualTo("2");
        Assertions.assertThat(parser.searchNumberRepresentation(2, false)).isEqualTo("3");
        Assertions.assertThat(parser.searchNumberRepresentation(3, false)).isEqualTo("8");
        Assertions.assertThat(parser.searchNumberRepresentation(4, true)).isEqualTo("11");
        Assertions.assertThat(parser.searchNumberRepresentation(4, false)).isEqualTo("12");

        parser = new Numbers2WordsParser(N2WOptionsBuilder.create(WordGender.MASCULINE, properties).build());
        Assertions.assertThat(parser.searchNumberRepresentation(1, false)).isEqualTo("4");
        Assertions.assertThat(parser.searchNumberRepresentation(2, false)).isEqualTo("5");
        Assertions.assertThat(parser.searchNumberRepresentation(3, false)).isEqualTo("9");
        Assertions.assertThat(parser.searchNumberRepresentation(4, true)).isEqualTo("11");
        Assertions.assertThat(parser.searchNumberRepresentation(4, false)).isEqualTo("12");

        parser = new Numbers2WordsParser(N2WOptionsBuilder.create(null, properties).build());
        Assertions.assertThat(parser.searchNumberRepresentation(1, false)).isEqualTo("6");
        Assertions.assertThat(parser.searchNumberRepresentation(2, false)).isEqualTo("7");
        Assertions.assertThat(parser.searchNumberRepresentation(3, false)).isEqualTo("10");
        Assertions.assertThat(parser.searchNumberRepresentation(4, true)).isEqualTo("11");
        Assertions.assertThat(parser.searchNumberRepresentation(4, false)).isEqualTo("12");
    }

    @Test
    public void testClassificationWordSearch() {
        Properties properties = new Properties();
        properties.put("integer.suffix.feminine.singular", "1");
        properties.put("integer.suffix.feminine.plural", "2");
        properties.put("integer.suffix.masculine.singular", "3");
        properties.put("integer.suffix.masculine.plural", "4");
        properties.put("integer-x2.suffix.feminine", "5");
        properties.put("integer-x2.suffix.masculine", "6");
        properties.put("integer.suffix.singular", "7");
        properties.put("integer.suffix.plural", "8");
        properties.put("fraction.suffix", "9");
        Numbers2WordsOptions options;
        Numbers2WordsParser parser;

        parser = new Numbers2WordsParser(N2WOptionsBuilder.create(WordGender.FEMININE, properties).build());
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 1)).isEqualTo("1");
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 2)).isEqualTo("2");
        Assertions.assertThat(parser.searchSuffixWord("integer-x2.suffix", 1)).isEqualTo("5");

        parser = new Numbers2WordsParser(N2WOptionsBuilder.create(WordGender.MASCULINE, properties).build());
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 1)).isEqualTo("3");
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 2)).isEqualTo("4");
        Assertions.assertThat(parser.searchSuffixWord("integer-x2.suffix", 2)).isEqualTo("6");

        parser = new Numbers2WordsParser(N2WOptionsBuilder.create(null, properties).build());
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 1)).isEqualTo("7");
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 2)).isEqualTo("8");
        Assertions.assertThat(parser.searchSuffixWord("fraction.suffix", 2)).isEqualTo("9");
    }
}
