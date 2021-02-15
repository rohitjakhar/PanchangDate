package com.rohitjakhar.panchangdate

import com.rohitjakhar.panchangdate.Helper.REV
import com.rohitjakhar.panchangdate.Helper.getAyanamsa
import com.rohitjakhar.panchangdate.Helper.getMoonLong
import com.rohitjakhar.panchangdate.Helper.getSunLong
import com.rohitjakhar.panchangdate.Utils.karan
import com.rohitjakhar.panchangdate.Utils.nakshatra
import com.rohitjakhar.panchangdate.Utils.rashi
import com.rohitjakhar.panchangdate.Utils.tithi
import com.rohitjakhar.panchangdate.Utils.yoga
import java.text.SimpleDateFormat
import java.util.*

object Panchangdate {

    // Calculate Panchang
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
        val ayanamsa = getAyanamsa(d.toDouble())
        val sunLongitude = getSunLong(d + ((time - zhe) / 24.0))
        val moonLongitude = getMoonLong(d + ((time - zhe) / 24.0))

        // Calculate Tithi and Paksha longitude
        var tithiMoonLongitude = moonLongitude + (if (moonLongitude < sunLongitude) 360 else 0)
        var tithiSunLongitude = sunLongitude

        var n: Int = ((tithiMoonLongitude - tithiSunLongitude) / 12).toInt()
        val tithi = tithi[n]

        val dPaksha = when {
            n <= 14 -> "Shukla"
            else -> "Krishna"
        }

        // Calculate Nakshatra
        tithiMoonLongitude = REV(moonLongitude + ayanamsa)
        val dNakshtra = nakshatra[(tithiMoonLongitude * 6 / 80).toInt()]

        // Calculate Yoga
        tithiMoonLongitude = moonLongitude + ayanamsa
        tithiSunLongitude = sunLongitude + ayanamsa
        val dYoga = yoga[(REV(tithiMoonLongitude + tithiSunLongitude) * 6 / 80).toInt()]

        // Calcualte Karana
        tithiMoonLongitude = moonLongitude + ((if (moonLongitude < sunLongitude) 360 else 0))
        tithiSunLongitude = sunLongitude

        n = ((tithiMoonLongitude - tithiSunLongitude) / 6).toInt()
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
        tithiMoonLongitude = REV(moonLongitude + ayanamsa)
        val dRashi = rashi[(tithiMoonLongitude / 30).toInt()]

        return Panchanga(
            dToday = dayOfWeek, dTithi = tithi, dPaksha = dPaksha, dNakshatra = dNakshtra,
            dRashi = dRashi, dYoga = dYoga, dKarana = dKarana
        )
    }

    fun getCurrentDate(calendar: Calendar) {
    }
}
