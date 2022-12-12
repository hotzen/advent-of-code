package day11

import day11p2.MonkeyOp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day11Test {

    @ParameterizedTest
    @CsvSource(
        "2,3, 1",
        "1,0, 1",
        "6,12, 6",
        "2,3, 1",
        "90, 100, 10"
    )
    fun gcd(a: Int, b: Int, gcd: Int) {
        assertThat(MonkeyOp.Mult.gcd(a, b))
            .isEqualTo(gcd)
    }
}
