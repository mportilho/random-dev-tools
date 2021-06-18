package io.github.mportilho.numbers2words;

import io.github.mportilho.assertions.Asserts;
import io.github.mportilho.numbers2words.i18n.ThousandSeparatorRule_PT_BR;
import io.github.mportilho.resources.ResourceLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

public class TestNumber2WordConversions {

    private static final Properties properties = new Properties();

    @BeforeAll
    public static void setup() throws Exception {
        Optional<InputStream> classpathResource = ResourceLoader.fromClasspath("numbers2words_PT_BR.txt");
        if (classpathResource.isEmpty()) {
            throw new Exception();
        }
        try (InputStream resource = classpathResource.get()) {
            Map<String, String> stringMap = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines()
                    .filter(Asserts::isNotEmpty).map(s -> s.split("=")).collect(Collectors.toMap(s -> s[0], s -> s[1]));
            properties.putAll(stringMap);
        }
    }

    @Test
    public void testExtractingNumbers() {
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("0"))).hasSize(1).contains(0);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("1"))).hasSize(1).contains(1);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("2"))).hasSize(1).contains(2);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("1234"))).hasSize(2).contains(1, 234);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("5500"))).hasSize(2).contains(5, 500);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("1234000"))).hasSize(3).contains(1, 234, 0);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("209670123418"))).hasSize(4).contains(209, 670, 123, 418);

    }

    @Test
    public void testConvert() throws Exception {
        Numbers2WordsParser parser = new Numbers2WordsParser(N2WOptionsBuilder.create(WordGender.MASCULINE, properties, new ThousandSeparatorRule_PT_BR()).build());
        System.out.println(parser.parse(54352231.009543459));
    }

    @Test
    public void testNumberInFullFormalFormatter() {
        N2WOptionsBuilder builder = N2WOptionsBuilder.create(WordGender.MASCULINE, properties, new ThousandSeparatorRule_PT_BR())
                .displayingIntegerUnit(false).displayingSingularWord(false);
        Numbers2WordsParser parser = new Numbers2WordsParser(builder.build());
        Assertions.assertThat(parser.parse(37)).isEqualTo("trinta e sete");
        Assertions.assertThat(parser.parse(237)).isEqualTo("duzentos e trinta e sete");
        Assertions.assertThat(parser.parse(207)).isEqualTo("duzentos e sete");
        Assertions.assertThat(parser.parse(5500)).isEqualTo("cinco mil e quinhentos");
        Assertions.assertThat(parser.parse(1300)).isEqualTo("mil e trezentos");
        Assertions.assertThat(parser.parse(8100)).isEqualTo("oito mil e cem");
        Assertions.assertThat(parser.parse(2125)).isEqualTo("dois mil cento e vinte e cinco");
        Assertions.assertThat(parser.parse(7491)).isEqualTo("sete mil quatrocentos e noventa e um");
        Assertions.assertThat(parser.parse(6510)).isEqualTo("seis mil quinhentos e dez");
        Assertions.assertThat(parser.parse(9030)).isEqualTo("nove mil e trinta");
        Assertions.assertThat(parser.parse(9003)).isEqualTo("nove mil e três");
        Assertions.assertThat(parser.parse(378027312)).isEqualTo("trezentos e setenta e oito milhões vinte e sete mil trezentos e doze");
        Assertions.assertThat(parser.parse(new BigDecimal("209670123418"))).isEqualTo("duzentos e nove bilhões seiscentos e setenta milhões cento e vinte e três mil quatrocentos e dezoito");
//        Assertions.assertThat(parser.parse(37)).isEqualTo("");

//        ToWordsConverter converter = new ToWordsConverter(ToWordsEnum.FORMAL_NUMBER_FORMATTER);
//        assertEquals(
//                "cinquenta e quatro milhões, trezentos e cinquenta e dois mil e duzentos e trinta e um inteiros e nove milhões, quinhentos e quarenta e três mil e quatrocentos e cinquenta e seis bilionésimos",
//                converter.toWords(54352231.009543459));
//        assertEquals("um inteiro e noventa e nove décimos de milésimo", converter.toWords(1.0099));
//        assertEquals("zero", converter.toWords(0));
//        assertEquals("quatro mil e quinhentos e trinta e um décimos de milésimo", converter.toWords(0.4531));
//        assertEquals("um inteiro e quatro mil e quinhentos e trinta e um décimos de milésimo", converter.toWords(1.4531));
//        assertEquals("cinco inteiros", converter.toWords(5));
//        assertEquals("um inteiro", converter.toWords(1));
//        assertEquals("um mil inteiros e cinquenta e quatro centésimos", converter.toWords(1000.54));
    }

}
