package io.github.mportilho.numbers2words;

import io.github.mportilho.assertions.Asserts;
import io.github.mportilho.numbers2words.i18n.ptbr.N2WLanguageRule_PT_BR;
import io.github.mportilho.numbers2words.options.N2WScaleDisplay;
import io.github.mportilho.numbers2words.options.N2WUnitDisplay;
import io.github.mportilho.numbers2words.options.N2WZeroDisplay;
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
    public void testExtractingIntegers() {
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("0"))).hasSize(1).contains(0);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("1"))).hasSize(1).contains(1);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("2"))).hasSize(1).contains(2);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("1000"))).hasSize(2).contains(1, 0);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("1234"))).hasSize(2).contains(1, 234);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("5500"))).hasSize(2).contains(5, 500);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("1234000"))).hasSize(3).contains(1, 234, 0);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("209670123418"))).hasSize(4).contains(209, 670, 123, 418);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal(2845123022L))).hasSize(4).contains(2, 845, 123, 22);
    }

    @Test
    public void testExtractingFractions() {
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("0.1"))).hasSize(1).contains(1);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("0.01"))).hasSize(1).contains(1);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("0.001"))).hasSize(1).contains(1);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("0.0001"))).hasSize(1).contains(1);
        Assertions.assertThat(Numbers2WordsParser.extractBlocks(new BigDecimal("0.5500"))).hasSize(1).contains(55);
    }

    @Test
    public void testIntegersInFullFormalFormatter() {
        N2WOptionsBuilder builder = N2WOptionsBuilder.create(WordGender.MASCULINE, properties, new N2WLanguageRule_PT_BR())
                .unitDisplay(N2WUnitDisplay.NONE);
        Numbers2WordsParser parser = new Numbers2WordsParser(builder.build());
        Assertions.assertThat(parser.parse(0)).isEqualTo("zero");
        Assertions.assertThat(parser.parse(1)).isEqualTo("um");
        Assertions.assertThat(parser.parse(2)).isEqualTo("dois");
        Assertions.assertThat(parser.parse(3)).isEqualTo("três");
        Assertions.assertThat(parser.parse(4)).isEqualTo("quatro");
        Assertions.assertThat(parser.parse(5)).isEqualTo("cinco");
        Assertions.assertThat(parser.parse(10)).isEqualTo("dez");
        Assertions.assertThat(parser.parse(18)).isEqualTo("dezoito");
        Assertions.assertThat(parser.parse(100)).isEqualTo("cem");
        Assertions.assertThat(parser.parse(200)).isEqualTo("duzentos");
        Assertions.assertThat(parser.parse(500)).isEqualTo("quinhentos");
        Assertions.assertThat(parser.parse(1000)).isEqualTo("mil");
        Assertions.assertThat(parser.parse(1001)).isEqualTo("mil e um");
        Assertions.assertThat(parser.parse(1010)).isEqualTo("mil e dez");
        Assertions.assertThat(parser.parse(1100)).isEqualTo("mil e cem");
        Assertions.assertThat(parser.parse(1101)).isEqualTo("mil cento e um");
        Assertions.assertThat(parser.parse(1111)).isEqualTo("mil cento e onze");
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
        Assertions.assertThat(parser.parse(516987)).isEqualTo("quinhentos e dezesseis mil novecentos e oitenta e sete");
        Assertions.assertThat(parser.parse(516100)).isEqualTo("quinhentos e dezesseis mil e cem");
        Assertions.assertThat(parser.parse(100254)).isEqualTo("cem mil duzentos e cinquenta e quatro");
        Assertions.assertThat(parser.parse(378027312)).isEqualTo("trezentos e setenta e oito milhões vinte e sete mil trezentos e doze");
        Assertions.assertThat(parser.parse(new BigDecimal("209670123418"))).isEqualTo("duzentos e nove bilhões seiscentos e setenta milhões cento e vinte e três mil quatrocentos e dezoito");
        Assertions.assertThat(parser.parse(2845123022L)).isEqualTo("dois bilhões oitocentos e quarenta e cinco milhões cento e vinte e três mil e vinte e dois");
        Assertions.assertThat(parser.parse(2000000000)).isEqualTo("dois bilhões");
        Assertions.assertThat(parser.parse(2000000001)).isEqualTo("dois bilhões e um");
        Assertions.assertThat(parser.parse(2000001000)).isEqualTo("dois bilhões e um mil");
        Assertions.assertThat(parser.parse(2001000000)).isEqualTo("dois bilhões e um milhão");
        Assertions.assertThat(parser.parse(2100000000)).isEqualTo("dois bilhões e cem milhões");
        Assertions.assertThat(parser.parse(2100001000)).isEqualTo("dois bilhões cem milhões e um mil");

        Assertions.assertThat(parser.parse(8425961637L)).isEqualTo("oito bilhões quatrocentos e vinte e cinco milhões novecentos e sessenta e um mil seiscentos e trinta e sete");
        Assertions.assertThat(parser.parse(425961637)).isEqualTo("quatrocentos e vinte e cinco milhões novecentos e sessenta e um mil seiscentos e trinta e sete");
        Assertions.assertThat(parser.parse(25961637)).isEqualTo("vinte e cinco milhões novecentos e sessenta e um mil seiscentos e trinta e sete");
        Assertions.assertThat(parser.parse(5961637)).isEqualTo("cinco milhões novecentos e sessenta e um mil seiscentos e trinta e sete");
        Assertions.assertThat(parser.parse(961637)).isEqualTo("novecentos e sessenta e um mil seiscentos e trinta e sete");
        Assertions.assertThat(parser.parse(61637)).isEqualTo("sessenta e um mil seiscentos e trinta e sete");
        Assertions.assertThat(parser.parse(1637)).isEqualTo("mil seiscentos e trinta e sete");
        Assertions.assertThat(parser.parse(637)).isEqualTo("seiscentos e trinta e sete");
    }

    @Test
    public void testIntegersWithSuffixInFullFormalFormatter() {
        N2WOptionsBuilder builder = N2WOptionsBuilder.create(WordGender.MASCULINE, properties, new N2WLanguageRule_PT_BR())
                .unitDisplay(N2WUnitDisplay.INTEGER_ONLY).zeroDisplay(N2WZeroDisplay.NONE);
        Numbers2WordsParser parser = new Numbers2WordsParser(builder.build());
        Assertions.assertThat(parser.parse(54352231.009543459)).isEqualTo("cinquenta e quatro milhões trezentos e cinquenta e dois mil duzentos e trinta e um inteiros e nove milhões quinhentos e quarenta e três mil quatrocentos e cinquenta e seis bilionésimos");
        Assertions.assertThat(parser.parse(1.0099)).isEqualTo("um inteiro e noventa e nove décimos de milésimo");
        Assertions.assertThat(parser.parse(0.4531)).isEqualTo("quatro mil quinhentos e trinta e um décimos de milésimo");
        Assertions.assertThat(parser.parse(1.4531)).isEqualTo("um inteiro e quatro mil quinhentos e trinta e um décimos de milésimo");
        Assertions.assertThat(parser.parse(1000.54)).isEqualTo("mil inteiros e cinquenta e quatro centésimos");
        Assertions.assertThat(parser.parse(1)).isEqualTo("um inteiro");
        Assertions.assertThat(parser.parse(5)).isEqualTo("cinco inteiros");
        Assertions.assertThat(parser.parse(10)).isEqualTo("dez inteiros");
    }

    @Test
    public void testFractionsInFullFormalFormatter() {
        N2WOptionsBuilder builder = N2WOptionsBuilder.create(WordGender.MASCULINE, properties, new N2WLanguageRule_PT_BR())
                .unitDisplay(N2WUnitDisplay.NONE).scaleDisplay(N2WScaleDisplay.FRACTION_ONLY).zeroDisplay(N2WZeroDisplay.NONE);
        Numbers2WordsParser parser = new Numbers2WordsParser(builder.build());
        Assertions.assertThat(parser.parse(0.1)).isEqualTo("um décimo");
        Assertions.assertThat(parser.parse(0.01)).isEqualTo("um centésimo");
        Assertions.assertThat(parser.parse(0.001)).isEqualTo("um milésimo");
        Assertions.assertThat(parser.parse(0.0001)).isEqualTo("um décimo de milésimo");
        Assertions.assertThat(parser.parse(0.000000001)).isEqualTo("um bilionésimo");
        Assertions.assertThat(parser.parse(0.8)).isEqualTo("oito décimos");
        Assertions.assertThat(parser.parse(0.08)).isEqualTo("oito centésimos");
        Assertions.assertThat(parser.parse(0.008)).isEqualTo("oito milésimos");
        Assertions.assertThat(parser.parse(0.0008)).isEqualTo("oito décimos de milésimo");
        Assertions.assertThat(parser.parse(0.000000008)).isEqualTo("oito bilionésimos");
        Assertions.assertThat(parser.parse(0.37)).isEqualTo("trinta e sete centésimos");
        Assertions.assertThat(parser.parse(0.237)).isEqualTo("duzentos e trinta e sete milésimos");
        Assertions.assertThat(parser.parse(0.85)).isEqualTo("oitenta e cinco centésimos");
        Assertions.assertThat(parser.parse(1.207)).isEqualTo("um e duzentos e sete milésimos");
        Assertions.assertThat(parser.parse(2.5500)).isEqualTo("dois e cinquenta e cinco centésimos");
        Assertions.assertThat(parser.parse(1000.1300)).isEqualTo("mil e treze centésimos");
        Assertions.assertThat(parser.parse(1002.1300)).isEqualTo("mil e dois e treze centésimos");
    }

}
