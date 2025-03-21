package com.example.speechtomorse

object MorseCodeConverter {
    private val morseMap = mapOf(
        'a' to ".-", 'b' to "-...", 'c' to "-.-.", 'd' to "-..",
        'e' to ".", 'f' to "..-.", 'g' to "--.", 'h' to "....",
        'i' to "..", 'j' to ".---", 'k' to "-.-", 'l' to ".-..",
        'm' to "--", 'n' to "-.", 'o' to "---", 'p' to ".--.",
        'q' to "--.-", 'r' to ".-.", 's' to "...", 't' to "-",
        'u' to "..-", 'v' to "...-", 'w' to ".--", 'x' to "-..-",
        'y' to "-.--", 'z' to "--..",
        '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--",
        '4' to "....-", '5' to ".....", '6' to "-....", '7' to "--...",
        '8' to "---..", '9' to "----.",
        ' ' to "/"
    )

    fun convert(input: String): String {
        if (input.isBlank()) return ""
        
        return input.lowercase()
            .map { char ->
                morseMap[char] ?: run {
                    when {
                        char.isLetterOrDigit() -> "?"  // Unknown letter/number
                        char.isWhitespace() -> "/"     // Space between words
                        else -> ""                     // Skip other characters
                    }
                }
            }
            .filter { it.isNotEmpty() }
            .joinToString(" ")
    }
}