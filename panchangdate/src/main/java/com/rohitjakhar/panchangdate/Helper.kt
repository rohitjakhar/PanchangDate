package com.rohitjakhar.panchangdate

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

internal object Helper {

    private val D2R = (Math.PI / 180.0)
    private val R2D = (180.0 / Math.PI)
    private var Ls: Double = 0.0
    private var Lm = 0.0
    private var Ms = 0.0
    private var Mm = 0.0

    fun REV(d: Double): Double {
        return d - floor(d / 360.0) * 360.0
    }

    // Calculate Ayanamsa
    fun getAyanamsa(d: Double): Double {
        val t = (d + 36523.5) / 36525
        val o = 259.183275 - 1934.142008333206 * t + 0.0020777778 * t * t
        val l = 279.696678 + 36000.76892 * t + 0.0003025 * t * t
        var ayan: Double =
            17.23 * sin((o) * D2R) + 1.27 * sin((l * 2) * D2R) - (5025.64 + 1.11 * t) * t
        // Based on Lahiri
        ayan = (ayan - 80861.27) / 3600.0
        return ayan
    }

    // Longitude of Sun
    fun getSunLong(d: Double): Double {
        val w: Double = 282.9404 + 4.70935e-5 * d
        val a: Double = 1.000000
        val e: Double = 0.016709 - 1.151e-9 * d
        val M: Double = REV(356.0470 + 0.9856002585 * d)
        Ms = M
        Ls = w + M

        var tmp: Double = M.times(D2R)
        val E: Double = M + R2D * e * sin(tmp) * (1 + e * cos(tmp))

        tmp = E * D2R
        val x: Double = cos(tmp) - e
        val y: Double = sin(tmp) * sqrt(1 - e * e)

        val r: Double = sqrt(x * x + y * y)
        val v: Double = REV(R2D * atan2(y, x))

        return REV(v + w)
    }

    // Longitude of Moon
    fun getMoonLong(d: Double): Double {
        val N: Double = 125.1228 - 0.0529538083 * d
        val i: Double = 5.1454
        val w: Double = REV(318.0634 + 0.1643573223 * d)
        val a: Double = 60.2666
        val e: Double = 0.054900
        val M: Double = REV(115.3654 + 13.0649929509 * d)
        val Mm: Double = M
        val Lm: Double = N + w + M

        // Calculate Eccentricity anamoly
        var tmp: Double = M * D2R
        var E: Double = M + R2D * e * sin(tmp) * (1 + e * cos(tmp))

        tmp = E * D2R
        var Et: Double = E - (E - R2D * e * sin(tmp) - M) / (1 - e * cos(tmp))

        do {
            E = Et
            tmp = E * D2R
            Et = E - (E - R2D * e * sin(tmp) - M) / (1 - e * cos(tmp))
        } while (E - Et > 0.005)

        tmp = E * D2R
        val x: Double = a * (cos(tmp) - e)
        val y: Double = a * sqrt(1 - e * e) * sin(tmp)

        val r: Double = sqrt(x * x + y * y)
        val v: Double = REV(R2D * atan2(y, x))

        tmp = D2R * N
        val tmp1: Double = D2R * (v + w)
        val tmp2: Double = D2R * i
        val xec: Double = r * (cos(tmp) * cos(tmp1) - sin(tmp) * sin(tmp1) * cos(tmp2))
        val yec: Double = r * (sin(tmp) * cos(tmp1) + cos(tmp) * sin(tmp1) * cos(tmp2))
        val zec: Double = r * sin(tmp1) * sin(tmp2)

        // Do some corrections
        val D: Double = Lm - Ls
        val F: Double = Lm - N

        var lon = R2D * atan2(yec, xec)

        lon += -1.274 * sin((Mm - 2 * D) * D2R)
        lon += +0.658 * sin((2 * D) * D2R)
        lon += -0.186 * sin((Ms) * D2R)
        lon += -0.059 * sin((2 * Mm - 2 * D) * D2R)
        lon += -0.057 * sin((Mm - 2 * D + Ms) * D2R)
        lon += +0.053 * sin((Mm + 2 * D) * D2R)
        lon += +0.046 * sin((2 * D - Ms) * D2R)
        lon += +0.041 * sin((Mm - Ms) * D2R)
        lon += -0.035 * sin((D) * D2R)
        lon += -0.031 * sin((Mm + Ms) * D2R)
        lon += -0.015 * sin((2 * F - 2 * D) * D2R)
        lon += +0.011 * sin((Mm - 4 * D) * D2R)

        return REV(lon)
    }
}
