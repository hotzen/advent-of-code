package day03

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.nio.charset.StandardCharsets

class Day03Test {

    @ParameterizedTest
    @CsvSource("""
a,1
b,2
z,26
A,27
B,28
Z,52      
""")
    fun `itemType should match priorities`(symbol: Char, priority: Int) {
        assertThat(ItemType(symbol).priority()).isEqualTo(priority)
    }
}
