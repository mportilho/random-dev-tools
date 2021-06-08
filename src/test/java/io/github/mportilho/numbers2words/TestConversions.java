package io.github.mportilho.numbers2words;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TestConversions {

    @Test
    public void testConvert() {
        Numbers2WordsParser parser = new Numbers2WordsParser();
        parser.parse(new BigDecimal("99912345.89812345"));
    }
}
