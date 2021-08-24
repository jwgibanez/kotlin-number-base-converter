package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.math.pow

fun main() {
    val scanner = Scanner(System.`in`)
    while (true) {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        when (val input1 = scanner.nextLine()) {
            "/exit" -> return
            else -> {
                val split = input1.split(" ")
                val sourceBase = split[0].toBigInteger()
                val targetBase = split[1].toBigInteger()
                loop@ while (true) {
                    print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back) ")
                    when(val input2 = scanner.nextLine()) {
                        "/back" -> break@loop
                        else -> {
                            val decimal: BigDecimal = if (sourceBase != BigInteger.TEN) converter(input2, sourceBase).toBigDecimal() else input2.toBigDecimal()
                            val target = if (targetBase == BigInteger.TEN) decimal else converter(decimal, targetBase, input2.contains("."))
                            println("Conversion result: $target\n")
                        }
                    }
                }
            }
        }
    }
}

fun converter(source: String, sourceBase: BigInteger): String {
    var sum = BigDecimal.ZERO
    val integer: String
    val fraction: String
    if (source.contains(".")) {
        val split = source.split(".")
        integer = split[0]
        fraction = split[1]
    } else {
        integer = source
        fraction = ""
    }
    // Integer part
    val reversed = integer.reversed()
    for (i in reversed.indices) {
        sum += if (reversed[i].isDigit())
            reversed[i].toString().toBigDecimal() * sourceBase.toBigDecimal().pow(i)
        else
            (10 + reversed[i].code - 'a'.code).toBigDecimal() * sourceBase.toBigDecimal().pow(i)
    }
    // Fractional part
    val sourceBaseDecimal = sourceBase.toDouble()
    var fractionSum = 0.0
    for (i in fraction.indices) {
        fractionSum += if (fraction[i].isDigit())
            fraction[i].toString().toDouble() / sourceBaseDecimal.pow(i + 1)
        else
            (10 + fraction[i].code - 'a'.code).toDouble() / sourceBaseDecimal.pow(i + 1)
    }
    return (sum + fractionSum.toBigDecimal()).toString()
}

fun converter(base10: BigDecimal, target: BigInteger, contains: Boolean) : String {
    var resultInteger = ""
    var resultFraction = ""
    val split = base10.toString().split(".")
    val integer = split[0].toBigInteger()
    // Integer part
    var remaining = integer
    while (remaining >= target) {
        resultInteger += convert((remaining % target).toInt())
        remaining /= target
    }
    resultInteger += convert(remaining.toInt())
    // Fraction part
    var ctr = 0
    var fraction = base10 - integer.toBigDecimal()
    if (contains) println(fraction)
    while (fraction > BigDecimal.ZERO) {
        if (ctr == 10) break
        val remainingDecimal = fraction * target.toBigDecimal()
        fraction = remainingDecimal - remainingDecimal.toInt().toBigDecimal()
        resultFraction += convert(remainingDecimal.toInt())
        ctr++
    }
    while (contains && resultFraction.length < 5) {
        // padding
        resultFraction += 0
    }
    return if
            (!contains) resultInteger.reversed()
    else resultInteger.reversed() + if (resultFraction.isNotEmpty()) ".${resultFraction.substring(0, 5)}" else ".00000"
}

fun convert(value: Int): String {
    return if (value < 10) value.toString()
    else ('a'.code + (value-10)).toChar().toString()
}