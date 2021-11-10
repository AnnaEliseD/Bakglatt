package com.example.skismoring.data.service

import android.util.Log
import org.json.JSONObject
import kotlin.math.roundToInt

class WaxChooser {



    private val optimalNewlyFallen = arrayOfNulls<String>(40)
    private val optimalOld = arrayOfNulls<String>(40)

    private var personalNewlyFallen = arrayOfNulls<String>(40)
    private var personalOld = arrayOfNulls<String>(40)

    init {
        fillNewlyFallen()
        fillOld()
    }

    private fun fillNewlyFallen() {
        for ( i in 0..10) optimalNewlyFallen[i] = "V05 Polar"
        for (i in 11..16) optimalNewlyFallen[i] = "V20 Green"
        for (i in 17..20) optimalNewlyFallen[i] = "V30 Blue"
        optimalNewlyFallen[13] = "VP30 Pro Light Blue"
        optimalNewlyFallen[18] = "VP40"
        optimalNewlyFallen[21] = "V40 Blue Extra"
        optimalNewlyFallen[22] = "VP45 Pro Blue/Violet"
        optimalNewlyFallen[23] = "V45 Violet Special"
        optimalNewlyFallen[24] = "VP55 Pro Violet"
        optimalNewlyFallen[25] = "V50 Violet"
        optimalNewlyFallen[26] = "V55 Red Special"
        optimalNewlyFallen[27] = "VP65 Pro Black Red"
        optimalNewlyFallen[28] = "V60 Red Silver"
    }

    private fun fillOld() {

        for (i in 0..9) optimalOld[i] = "V05 Polar"
        for (i in 10..12) optimalOld[i] = "V20 Green"
        for (i in 13..18) optimalOld[i] = "V30 Blue"
        for (i in 15..16) optimalOld[i] = "VP30 Pro Light Blue"
        optimalOld[19] = "V40 Blue Extra"
        optimalOld[20] = "V40 Blue Extra"
        optimalOld[21] = "VP45 Pro Blue/Violet"
        optimalOld[22] = "V45 Violet Special"
        optimalOld[23] = "V50 Violet"
        optimalOld[24] = "V55 Red Special"
        optimalOld[25] = "V60 Red Silver"
        optimalOld[26] = "V60 Red Silver"
        optimalOld[27] = "V60 Red Silver"
    }

    /* Fill the personal list with the personal waxes based on their repsective position
    * in the optimal list*/
    fun fillPersonalLists(waxes : MutableList<String?>) {
        personalNewlyFallen = arrayOfNulls<String>(40)
        personalOld = arrayOfNulls<String>(40)

        for (i in optimalNewlyFallen.indices) {
            if (optimalNewlyFallen[i] in waxes) {
                personalNewlyFallen[i] = optimalNewlyFallen[i]
            }
        }
        for (i in optimalOld.indices) {
            if (optimalOld[i] in waxes) {
                personalOld[i] = optimalOld[i]
            }
        }
    }

    /* Returns a wax name and accuracy in strings
    * This is taking into account the users chosen wax*/
    fun choosePersonalWax(temperature: Int, snowType: String): List<String> {
        var counter1 = temperature + 25
        var counter2 = temperature + 25
        if (counter1 < 0) {
            counter1 = 0
            counter2 = 0
        }

        if (counter2 >= 40){
            counter2 = 39
            counter1 = 39
        }

        val returnValue = mutableListOf("", "")

        var tempOffset: Int
        if (snowType == "new") {
            while (personalNewlyFallen[counter1] == null && personalNewlyFallen[counter2] == null) {
                if (counter1 < personalNewlyFallen.size-1) counter1 ++
                if (counter2 > 0) counter2 --
                if (counter1 >= personalNewlyFallen.size-1 && counter2 <= 0) return listOf("", "Smøring mangler")
            }
            if (personalNewlyFallen[counter2] != null) {
                returnValue[0] = personalNewlyFallen[counter2].toString()
                tempOffset = temperature + 25 - counter2
                when(tempOffset){
                    0 -> returnValue[1] = "Perfekt match"
                    in 0..4 -> returnValue[1] = "Ganske god match"
                    else -> returnValue[1] = "Dårlig match"
                }
            }
            if (personalNewlyFallen[counter1] != null) {
                returnValue[0] = personalNewlyFallen[counter1].toString()
                tempOffset = counter1 - temperature - 25
                when(tempOffset){
                    0 -> returnValue[1] = "Perfekt match"
                    in 0..4 -> returnValue[1] = "Ganske god match"
                    else -> returnValue[1] = "Dårlig match"
                }
            }
        }
        else if (snowType == "old") {
            while (personalOld[counter1] == null && personalOld[counter2] == null) {
                if (counter1 < personalOld.size-1) counter1 ++
                if (counter2 > 0) counter2 --
                if (counter1 >= personalOld.size-1 && counter2 <= 0) return listOf("", "Smøring mangler")
            }
            if (personalOld[counter2] != null) {
                returnValue[0] = personalOld[counter2].toString()
                tempOffset = temperature + 25 - counter2
                when(tempOffset){
                    0 -> returnValue[1] = "Perfekt match"
                    in 0..4 -> returnValue[1] = "Ganske god match"
                    else -> returnValue[1] = "Dårlig match"
                }
            }
            if (personalOld[counter1] != null) {
                returnValue[0] = personalOld[counter1].toString()
                tempOffset = counter1 - temperature - 25
                when(tempOffset){
                    0 -> returnValue[1] = "Perfekt match"
                    in 0..4 -> returnValue[1] = "Ganske god match"
                    else -> returnValue[1] = "Dårlig match"
                }
            }
        }
        return returnValue
    }

    /* Snow type either "new" or "old", small font
    * Returns a name of the wax based on the input */
    fun chooseOptimalWax(temperature: Float, snowType: String): String? {
        val index = temperature.roundToInt() + 25
        if (index > 27) return "V60 Red Silver"
        if (index < 0) return "V05 Polar"
        Log.v("temp in wax", temperature.toString())
        return when (snowType) {
            "new" -> optimalNewlyFallen[index]
            "old" ->  optimalOld[index]
            else -> "Could not choose wax - wrong input"
        }
    }
}