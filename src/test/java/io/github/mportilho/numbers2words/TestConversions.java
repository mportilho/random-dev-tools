package io.github.mportilho.numbers2words;

import io.github.mportilho.assertions.Asserts;
import io.github.mportilho.resources.ResourceLoader;
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
        Numbers2WordsOptions options = new Numbers2WordsOptions(Numbers2WordsOptions.WordGender.MASCULINE, properties);
        Numbers2WordsParser parser = new Numbers2WordsParser(options);
        parser.parse(new BigDecimal("99912345.89812345"));
    }
}
