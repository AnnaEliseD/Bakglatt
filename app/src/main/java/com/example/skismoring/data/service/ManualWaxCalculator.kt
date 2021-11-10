package com.example.skismoring.data.service

import com.example.skismoring.R
import kotlin.math.roundToInt

class ManualWaxCalculator {

    private val dryNewlyFallen = arrayOfNulls<String>(43)
    private val dryFineGrained = arrayOfNulls<String>(43)
    private val dryOldConverted = arrayOfNulls<String>(43)
    private val dryCoarseGrained = arrayOfNulls<String>(43)
    private val wetNewlyFallen = arrayOfNulls<String>(43)
    private val wetFineGrained = arrayOfNulls<String>(43)
    private val wetOldConverted = arrayOfNulls<String>(43)
    private val wetCoarseGrained = arrayOfNulls<String>(43)

    private var personalList: MutableList<String?> = mutableListOf()
    private var currentPersonalList = arrayOfNulls<String>(43)


    init {
        fillLists()
    }

    /* Returns a wax name and accuracy in strings
    * This is taking into account the users chosen wax*/
    fun getPersonalWax(snowType: String, temp: Float): Pair<String, String>{
        val listPointer = listPointer(temp)
        val snowTypeList: Array<String?>
        // Decide which list to access based on snowtype input
        snowType.let {
            snowTypeList = when (it){
                "Tørr nyfallen" ->  dryNewlyFallen
                "Tørr finkornet" -> dryFineGrained
                "Tørr omdannet" -> dryOldConverted
                "Tørr grovkornet" -> dryCoarseGrained
                "Våt nyfallen" -> wetNewlyFallen
                "Våt finkornet" -> wetFineGrained
                "Våt omdannet" -> wetOldConverted
                "Våt grovkornet" -> wetCoarseGrained
                else -> emptyArray()
            }
        }
        var counter1 = listPointer
        var counter2 = listPointer
        val returnValue = mutableListOf("","")
        var tempOffset: Int

        fillCurrentList(snowTypeList)

        while (currentPersonalList[counter1] == null && currentPersonalList[counter2] == null) {
            if (counter1 < currentPersonalList.size-1) counter1 ++
            if (counter2 > 0) counter2 --
            if (counter1 >= currentPersonalList.size-1 && counter2 <= 0) return Pair("", "Smøring mangler")
        }

        if (currentPersonalList[counter2] != null) {
            returnValue[0] = currentPersonalList[counter2].toString()
            tempOffset = temp.roundToInt() + 25 - counter2
            when(tempOffset){
                0 -> returnValue[1] = "Perfekt match"
                in 0..4 -> returnValue[1] = "Ganske god match"
                else -> returnValue[1] = "Dårlig match"
            }
        }
        if (currentPersonalList[counter1] != null) {
            returnValue[0] = currentPersonalList[counter1].toString()
            tempOffset = counter1 - temp.roundToInt() - 25
            when(tempOffset){
                0 -> returnValue[1] = "Perfekt match"
                in 0..4 -> returnValue[1] = "Ganske god match"
                else -> returnValue[1] = "Dårlig match"
            }
        }
        return Pair(returnValue[0], returnValue[1])

    }

    /* Returns a wax name and accuracy in strings */
    fun getOptimalWax(snowType:String, temp: Float): Pair<String, String> {
        val listPointer = listPointer(temp)
        val snowTypeList: Array<String?>
        snowType.let {
            snowTypeList = when (it){
                "Tørr nyfallen" ->  dryNewlyFallen
                "Tørr finkornet" -> dryFineGrained
                "Tørr omdannet" -> dryOldConverted
                "Tørr grovkornet" -> dryCoarseGrained
                "Våt nyfallen" -> wetNewlyFallen
                "Våt finkornet" -> wetFineGrained
                "Våt omdannet" -> wetOldConverted
                "Våt grovkornet" -> wetCoarseGrained
                else -> emptyArray()
            }
        }

        val waxString = snowTypeList[listPointer]
                ?: return Pair("", "Finnes ikke smøring for denne kombinasjonen")
        return Pair(waxString, "")

    }

    /* Returns the listPointer for the waxArrays based on temperature*/
    private fun listPointer(temp: Float): Int {
        val listPointer: Int
        val roundedTemp = temp.roundToInt()
        roundedTemp.let {
            listPointer = when {
                it <= -1 -> it + 25
                it >= 1 -> it + 27
                else -> it + 26
            }
        }

        return listPointer
    }



    private fun fillLists() {
        //fill list 1:
        for (i in 0..8) dryNewlyFallen[i] = "V05 Polar"
        for (i in 9..14) dryNewlyFallen[i] = "V20 Green"
        for (i in 15..18) dryNewlyFallen[i] = "V30 Blue"
        for (i in 19..22) dryNewlyFallen[i] = "V40 Blue Extra"
        for (i in 23..24) dryNewlyFallen[i] = "V45 Violet Special"
        for (i in 25..26) dryNewlyFallen[i] = "VR50 Fiolett"

        //fill list 2
        for (i in 0..6) dryFineGrained[i] = "V05 Polar"
        for (i in 7..13) dryFineGrained[i] = "V20 Green"
        for (i in 14..17) dryFineGrained[i] = "V30 Blue"
        for (i in 18..21) dryFineGrained[i] = "V40 Blue Extra"
        dryFineGrained[22] = "V45 Violet Special"
        for (i in 23..24) dryFineGrained[i] = "VP65 Pro Black Red"
        for (i in 25..26) dryFineGrained[i] = "VP65 Pro Black Red"
        dryFineGrained[27] = "VR62 Hard Klistervoks"

        //fill list 3
        for (i in 0..3) dryOldConverted[i] = "V05 Polar"
        for (i in 4..12) dryOldConverted[i] = "V20 Green"
        for (i in 13..16) dryOldConverted[i] = "V30 Blue"
        for (i in 17..20) dryOldConverted[i] = "V40 Blue Extra"
        for (i in 21..22) dryOldConverted[i] = "V45 Violet Special"
        dryOldConverted[23] = "VR55N Myk Fiolett"
        for (i in 24..27) dryOldConverted[i] = "VP65 Pro Black Red"

        //fill list 4
        for (i in 14..20) dryCoarseGrained[i] = "KX30 Blue Ice Klister"
        for (i in 21..22) dryCoarseGrained[i] = "KX35 Violet Special"
        for (i in 23..26) dryCoarseGrained[i] = "KN33 Nero Klister"

        //fill list 5
        for (i in 24..26) wetNewlyFallen[i] = "VR55N Myk Fiolett"
        for (i in 27..29) wetNewlyFallen[i] = "VP65 Pro Black Red"
        for (i in 30..31) wetNewlyFallen[i] = "VR70 Rød Klistervoks"

        //fill list 6
        for (i in 23..26) wetFineGrained[i] = "VR55N Myk Fiolett"
        for (i in 27..29) wetFineGrained[i] = "VP65 Pro Black Red"
        wetFineGrained[30] = "KX45 Violet Klister"
        wetFineGrained[31] = "KX40S Silver Klister"

        //fill list 7
        for (i in 23..26) wetOldConverted[i] = "VP65 Pro Black Red"
        for (i in 27..28) wetOldConverted[i] = "KN33 Nero Klister"
        for (i in 29..32) wetOldConverted[i] = "KN44 Black Nero Klister"

        //fill list 8
        for (i in 21..28) wetCoarseGrained[i] = "KX35 Violet Special"
        wetCoarseGrained[29] = "KN44 Black Nero Klister"
        wetCoarseGrained[30] = "K22 Universal Klister"
        for (i in 31..32) wetCoarseGrained[i] = "KX65 Red Klister"
        for (i in 33..42) wetCoarseGrained[i] = "KX75 Red Extra Wet"
    }

    fun fillPersonalList(list: MutableList<String?>){
        personalList = list
    }

    fun fillCurrentList(targetList: Array<String?>){
        currentPersonalList = arrayOfNulls<String?>(43)
        for (i in targetList.indices) {
            if (targetList[i] in personalList) {
                currentPersonalList[i] = targetList[i]
            }
        }
    }
}