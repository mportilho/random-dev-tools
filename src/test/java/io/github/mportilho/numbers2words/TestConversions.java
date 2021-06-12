package io.github.mportilho.numbers2words;

import io.github.mportilho.assertions.Asserts;
import io.github.mportilho.resources.ResourceLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class TestConversions {

    @Test
    public void testConvert() throws Exception {
        Properties properties = new Properties();
        Optional<InputStream> classpathResource = ResourceLoader.fromClasspath("numbers2words_PT_BR.txt");
        if (classpathResource.isEmpty()) {
            throw new Exception();
        }
        try (InputStream resource = classpathResource.get()) {
            Map<String, String> stringMap = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines()
                    .filter(Asserts::isNotEmpty).map(s -> s.split("=")).collect(Collectors.toMap(s -> s[0], s -> s[1]));
            properties.putAll(stringMap);
        }
        Numbers2WordsOptions options = new Numbers2WordsOptions(WordGender.MASCULINE, properties);
        Numbers2WordsParser parser = new Numbers2WordsParser(options);
        System.out.println(parser.parse(54352231.009543459));
    }

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

        options = new Numbers2WordsOptions(WordGender.FEMININE, properties);
        parser = new Numbers2WordsParser(options);
        Assertions.assertThat(parser.searchWord("integer", "number", 1, false)).isEqualTo("2");
        Assertions.assertThat(parser.searchWord("integer", "number", 2, false)).isEqualTo("3");
        Assertions.assertThat(parser.searchWord("integer", "number", 3, false)).isEqualTo("8");
        Assertions.assertThat(parser.searchWord("integer", "number", 4, true)).isEqualTo("11");
        Assertions.assertThat(parser.searchWord("integer", "number", 4, false)).isEqualTo("12");

        options = new Numbers2WordsOptions(WordGender.MASCULINE, properties);
        parser = new Numbers2WordsParser(options);
        Assertions.assertThat(parser.searchWord("integer", "number", 1, false)).isEqualTo("4");
        Assertions.assertThat(parser.searchWord("integer", "number", 2, false)).isEqualTo("5");
        Assertions.assertThat(parser.searchWord("integer", "number", 3, false)).isEqualTo("9");
        Assertions.assertThat(parser.searchWord("integer", "number", 4, true)).isEqualTo("11");
        Assertions.assertThat(parser.searchWord("integer", "number", 4, false)).isEqualTo("12");

        options = new Numbers2WordsOptions(null, properties);
        parser = new Numbers2WordsParser(options);
        Assertions.assertThat(parser.searchWord("integer", "number", 1, false)).isEqualTo("6");
        Assertions.assertThat(parser.searchWord("integer", "number", 2, false)).isEqualTo("7");
        Assertions.assertThat(parser.searchWord("integer", "number", 3, false)).isEqualTo("10");
        Assertions.assertThat(parser.searchWord("integer", "number", 4, true)).isEqualTo("11");
        Assertions.assertThat(parser.searchWord("integer", "number", 4, false)).isEqualTo("12");
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

        options = new Numbers2WordsOptions(WordGender.FEMININE, properties);
        parser = new Numbers2WordsParser(options);
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 1)).isEqualTo("1");
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 2)).isEqualTo("2");
        Assertions.assertThat(parser.searchSuffixWord("integer-x2.suffix", 1)).isEqualTo("5");

        options = new Numbers2WordsOptions(WordGender.MASCULINE, properties);
        parser = new Numbers2WordsParser(options);
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 1)).isEqualTo("3");
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 2)).isEqualTo("4");
        Assertions.assertThat(parser.searchSuffixWord("integer-x2.suffix", 2)).isEqualTo("6");

        options = new Numbers2WordsOptions(null, properties);
        parser = new Numbers2WordsParser(options);
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 1)).isEqualTo("7");
        Assertions.assertThat(parser.searchSuffixWord("integer.suffix", 2)).isEqualTo("8");
        Assertions.assertThat(parser.searchSuffixWord("fraction.suffix", 2)).isEqualTo("9");
    }
}
