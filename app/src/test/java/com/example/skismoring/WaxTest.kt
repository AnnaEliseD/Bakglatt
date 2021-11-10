package com.example.skismoring

import com.example.skismoring.data.historicData.LatestWeather
import com.example.skismoring.data.manager.RecommendationRepository
import com.example.skismoring.data.manager.WaxRepository
import com.example.skismoring.data.model.Wax
import com.example.skismoring.data.service.WaxChooser
import com.example.skismoring.ui.common.AlertDialogFragment
import com.example.skismoring.ui.map.MapFragment
import com.mapbox.mapboxsdk.geometry.LatLng
import io.mockk.*
import org.junit.Assert
import org.junit.Test

class WaxTest {

    @Test
    fun getWObject() {
        val waxCho =mockk<WaxChooser>()
        every { waxCho.chooseOptimalWax(any(),any())} returns "V05 Polar"
        waxCho.chooseOptimalWax(12.toFloat(), "new")
    }


    @Test
    fun waxIsNotNull() {
        val mapFrag = mockk<MapFragment>()
        val recRep = mockk<RecommendationRepository>()
        val waxCho = mockk<WaxChooser>()

        every { mapFrag.onMapClick(LatLng(10.toDouble(),10.toDouble())) } throws RuntimeException("error")

        every { mapFrag.onMapClick(any())} returns true

        val adf = mockk<AlertDialogFragment>()
        every{adf.onCreateDialog(any())} throws IllegalStateException("")

           // every { mockk<WaxChooser>().chooseOptimalWax(any(),any()) } returns ""
        every {
            mockk<WaxRepository>().getWaxObject(any())
            mockk<WaxRepository>().getOptimalWax(any(), any())
            } returns Wax("", "")
        every { mockk<WaxRepository>().getPersonalWax(any(), any()) } returns Pair(null, "")
        coEvery { mockk<RecommendationRepository>().getRecommendation(any(), any()) }


        //every { waxCho.chooseOptimalWax(any(),any()) } returns "V05 Polar"
        /*
        val latWea = mockk<LatestWeather> {
            coEvery { mockk<LatestWeather>().fromGeoToSnowType(any(), any()) } returns mutableListOf()
        }*/



       // waxCho.chooseOptimalWax(12.toFloat(), "new")
        //waxCho.choosePersonalWax(12, "new")

        mockk<WaxRepository>().getOptimalWax(12.toFloat(), "new")

        /*verify {
            mockk<WaxChooser>().chooseOptimalWax(any(), any())
        }*/

        mapFrag.onMapClick(LatLng(10.toDouble(),10.toDouble()))


        verifyOrder {
            mapFrag.onMapClick(any())
            waxCho.chooseOptimalWax(any(),any())
            waxCho.choosePersonalWax(any(),any())
            mockk<WaxRepository>().getWaxObject(any())
            mockk<WaxRepository>().getPersonalWax(any(),any())
            mockk<WaxRepository>().getOptimalWax(any(),any())
        }


        Assert.assertEquals("V05 Polar", mockk<WaxChooser>().chooseOptimalWax((-50).toFloat(), "new"))
        Assert.assertEquals("V60 Red Silver", mockk<WaxChooser>().chooseOptimalWax((50).toFloat(), "new"))
        mockk<WaxChooser>().fillPersonalLists(mutableListOf("V05 Polar", "V60 Red Silver"))
        Assert.assertEquals("V60 Red Silver", mockk<WaxChooser>().choosePersonalWax(5, "new")[0])
        Assert.assertEquals("V05 Polar", mockk<WaxChooser>().choosePersonalWax((-10), "new")[0])
        Assert.assertEquals("V60 Red Silver", mockk<WaxChooser>().choosePersonalWax(50, "new")[0])
        Assert.assertEquals("V05 Polar", mockk<WaxChooser>().choosePersonalWax((-50), "new")[0])

    }
}