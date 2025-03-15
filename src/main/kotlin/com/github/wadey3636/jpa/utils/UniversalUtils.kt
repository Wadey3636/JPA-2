package com.github.wadey3636.jpa.utils



//10,000,000
//10m
object UniversalUtils {

    /**
     * Abbreviates a number
     * Ex: 10,000,000 -> 10.0m
     *
     * @param n Double which is abbreviated
     */
    fun abbreviateNumber(n: Double): String {
        val nABS = kotlin.math.abs(n)
        return when {
            nABS < 1_000 -> n.toString()
            nABS in 1_000.0..999_999.0 -> "${"%.1f".format(n / 1_000)}K"
            nABS in 1_000_000.0..999_999_999.0 -> "${"%.1f".format(n / 1_000_000)}M"
            nABS in 1_000_000_000.0..999_999_999_999.0 -> "${"%.1f".format(n / 1_000_000_000)}B"
            nABS >= 1_000_000_000_000.0 -> "${"%.1f".format(n / 1_000_000_000_000)}T"
            else -> n.toString()
        }


    }




    fun abbreviateNumber(n: Int): String {
        return abbreviateNumber(n.toDouble())
    }

    fun abbreviateNumber(n: Float): String {
        return abbreviateNumber(n.toDouble())
    }


    val Int.abbreviateNumber: String
        get() = abbreviateNumber(this)

    val Float.abbreviateNumber: String
        get() = abbreviateNumber(this)

    val Double.abbreviateNumber: String
        get() = abbreviateNumber(this)


}