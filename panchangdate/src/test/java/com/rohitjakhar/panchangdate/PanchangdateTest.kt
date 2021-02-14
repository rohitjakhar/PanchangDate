package com.rohitjakhar.panchangdate

import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class PanchangdateTest {

    @Test
    fun `test date`() {

        val curren = Calendar.getInstance()

        val dd = curren.get(Calendar.DAY_OF_MONTH)
        val mm = curren.get(Calendar.MONTH) + 1
        val yy = curren.get(Calendar.YEAR)
        val hh = curren.get(Calendar.HOUR)
        val cc = String.format("%s/%s/%s", dd, mm, yy)

        val inputDatee = SimpleDateFormat("dd/MM/yyyy").parse(cc)
        val calendar = Calendar.getInstance()
        calendar.time = inputDatee

        // val tday = calendar.getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US)

        val p = Panchangdate().getPanchangDate(dd, mm, yy, hh.toDouble(), 5.30)
        p.dKarana
        println(p.dKarana)
        println(p.dNakshatra)
        println(p.dPaksha)
        println(p.dRashi)
        println(p.dTithi)
        println(p.dToday)
        println(p.dYoga)

        println(dd)
        println(mm)
        println(yy)
        println(hh)
        println(dd)
    }
}
