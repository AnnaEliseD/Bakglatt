package com.example.skismoring

import com.example.skismoring.data.historicData.LatestWeather
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class NavigationTest {

    @Test
    fun locationTest() {

        val latestWeather = LatestWeather()

        assertNotNull(latestWeather.findNearestFunctioningSource(123.toFloat(), 123.toFloat()))
        assertNotNull(latestWeather.findNearestFunctioningSource(0.toFloat(), 0.toFloat()))
        assertEquals("SN18950", latestWeather.findNearestFunctioningSource(0.toFloat(), 0.toFloat()))
        assertEquals("SN18500", latestWeather.findNearestFunctioningSource(100.toFloat(), 100.toFloat()))

        val testSnowType = (suspend {  latestWeather.fromGeoToSnowType(10.toFloat(), 10.toFloat())})
        assertNotNull(testSnowType)

        val mockedFile = mockk<LatestWeather>()
        every{
            mockedFile.findNearestFunctioningSource(100.toFloat(), 100.toFloat())
        } returns "SN18500"

        verify{
            mockedFile.findNearestFunctioningSource(100.toFloat(), 100.toFloat())
        }
    }
}