package com.rohitjakhar.panchangdate

import com.rohitjakhar.panchangdate.Utils.karan
import com.rohitjakhar.panchangdate.Utils.nakshatra
import com.rohitjakhar.panchangdate.Utils.rashi
import com.rohitjakhar.panchangdate.Utils.tithi
import com.rohitjakhar.panchangdate.Utils.yoga
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

class Panchangdate {

    private val D2R = (Math.PI / 180.0)
    private val R2D = (180.0 / Math.PI)
    private var Ls: Double = 0.0
    private var Lm = 0.0
    private var Ms = 0.0
    private var Mm = 0.0

    private fun REV(d: Double): Double {
        return d - floor(d / 360.0) * 360.0
    }

    // Calculate Ayanamsa
    private fun getAyanmsa(d: Double): Double {
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
    private fun getSunLong(d: Double): Double {
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
    private fun getMoonLong(d: Double): Double {
        val N: Double = 125.1228 - 0.0529538083 * d
        val i: Double = 5.1454
        val w: Double = REV(318.0634 + 0.1643573223 * d)
        val a: Double = 60.2666
        val e: Double = 0.054900
        val M: Double = REV(115.3654 + 13.0649929509 * d)
        var Mm: Double = M
        var Lm: Double = N + w + M

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

    // Calculate Panchanga
    fun getPanchangDate(dd: Int, mm: Int, yy: Int, time: Double, zhe: Double): Panchanga {


        val inputDateStr = String.format("%s/%s/%s", dd, mm, yy)
        val inputDate: Date = SimpleDateFormat("dd/MM/yyyy").parse(inputDateStr)

        val calendar = Calendar.getInstance()
        calendar.time = inputDate

        val dayOfWeek =
            calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).toUpperCase()

        // Calculate day number since 2000 Jan 0.0 TDT
        val d = (367 * yy - 7 * (yy + (mm + 9) / 12) / 4 + 275 * mm / 9 + dd - 730530)

        // Calculate Ayanamsa, Moon and Sun Longitude
        val ayanamsa = getAyanmsa(d.toDouble())
        val sLon = getSunLong(d + ((time - zhe) / 24.0))
        val mLon = getMoonLong(d + ((time - zhe) / 24.0))

        // Calculate Tithi and Paksha
        var tmLon = mLon + (if (mLon < sLon) 360 else 0)
        var tsLon = sLon

        var n: Int = ((tmLon - tsLon) / 12).toInt()
        val tithi = tithi[n]

        val dPaksha = when {
            n <= 14 -> "Shukla"
            else -> "Krishna"
        }

        // Calculate Nakshatra
        tmLon = REV(mLon + ayanamsa)
        val dNakshtra = nakshatra[(tmLon * 6 / 80).toInt()]

        // Calculate Yoga
        tmLon = mLon + ayanamsa
        tsLon = sLon + ayanamsa
        val dYoga = yoga[(REV(tmLon + tsLon) * 6 / 80).toInt()]

        // Calcualte Karana
        tmLon = mLon + ((if (mLon < sLon) 360 else 0))
        tsLon = sLon

        n = ((tmLon - tsLon) / 6).toInt()
        when {
            n == 0 -> {
                n = 10
            }
            n >= 57 -> {
                n = -50
            }
            n in 1..56 -> {
                n = (n - 1) - ((n - 1) / 7 * 7)
            }
        }

        val dKarana = karan[n]

        // Calculate the rashi in which the moon isn present
        tmLon = REV(mLon + ayanamsa)
        val dRashi = rashi[(tmLon / 30).toInt()]

        return Panchanga(
            dToday = dayOfWeek, dTithi = tithi, dPaksha = dPaksha, dNakshatra = dNakshtra,
            dRashi = dRashi, dYoga = dYoga, dKarana = dKarana
        )
    }
}
