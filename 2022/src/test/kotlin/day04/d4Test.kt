package day04

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day03Test {

    @ParameterizedTest
    @MethodSource("pairData1")
    fun `Pair should Parse and full-contains`(line: String, hasFullContains: Boolean) {
        println(line)
        assertThat(Pair.from(line).hasFullContainments())
            .isEqualTo(hasFullContains)
    }

    @ParameterizedTest
    @MethodSource("pairData2")
    fun `Pair should Parse and overlap-at-all`(line: String, overlapsAtAll: Boolean) {
        println(line)
        assertThat(Pair.from(line).overlapsAtAll())
            .isEqualTo(overlapsAtAll)
    }

    companion object {
        @JvmStatic
        fun pairData1(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("2-4,6-8", false),
                Arguments.of("2-3,4-5", false),
                Arguments.of("5-7,7-9", false),
                Arguments.of("2-8,3-7", true),
                Arguments.of("6-6,4-6", true),
                Arguments.of("2-6,4-8", false),
            )
        }

        @JvmStatic
        fun pairData2(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("2-4,6-8", false),
                Arguments.of("2-3,4-5", false),
                Arguments.of("5-7,7-9", true),
                Arguments.of("2-8,3-7", true),
                Arguments.of("6-6,4-6", true),
                Arguments.of("2-6,4-8", true),
            )
        }
    }
}
