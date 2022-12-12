package day06

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day6Test {

    @ParameterizedTest
    @CsvSource(
        "mjqjpqmgbljsphdztnvjfqwrcgsmlb, 7",
        "bvwbjplbgvbhsrlpgdmjqwftvncz, 5",
        "nppdvjthqldpwncqszvftbrmjlhg, 6",
        "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg, 10",
        "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw, 11"
    )
    fun `determine marker index`(stream: String, expectedIndex: Int) {
        println("$stream/$expectedIndex")
//        assertThat(determineIndex(stream))
//            .isEqualTo(expectedIndex)
    }
}
