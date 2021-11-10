package com.example.skismoring

import com.example.skismoring.data.manager.WaxRepository
import com.example.skismoring.data.service.WaxChooser
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WaxObjectTest {

    @Test
    fun waxObjectNotNull() {
        val waxRep = mockk<WaxRepository>()
        assertNotNull(waxRep.getWaxObject("V05 Polar"))
        assertNull(waxRep.getWaxObject("Hei"))
    }
}