package com.example.skismoring.data.manager

import android.util.Log
import com.example.skismoring.data.local.WaxDao
import com.example.skismoring.data.model.Wax
import com.example.skismoring.data.service.ManualWaxCalculator
import com.example.skismoring.data.service.WaxChooser
import kotlin.math.roundToInt

class WaxRepository(private val waxDao: WaxDao?) {

    val waxes: MutableList<Wax> = mutableListOf()
    var personalizedWaxes: List<Wax> = listOf()
    var waxCalculator: ManualWaxCalculator = ManualWaxCalculator()
    private val waxChooser = WaxChooser()

    init {
        waxes.plusAssign(Wax("V05 Polar","@drawable/v05" ))
        waxes.plusAssign(Wax("V20 Green", "@drawable/v20"))
        waxes.plusAssign(Wax("V30 Blue", "@drawable/v30"))
        waxes.plusAssign(Wax("V40 Blue Extra", "@drawable/v40"))
        waxes.plusAssign(Wax("V45 Violet Special", "@drawable/v45"))
        waxes.plusAssign(Wax("V50 Violet", "@drawable/v50"))
        waxes.plusAssign(Wax("V55 Red Special", "@drawable/v55"))
        waxes.plusAssign(Wax("V60 Red Silver", "@drawable/v60"))
        waxes.plusAssign(Wax("VP30 Pro Light Blue", "@drawable/vp30"))
        waxes.plusAssign(Wax("VP40 Pro Blue", "@drawable/vp40"))
        waxes.plusAssign(Wax("VP45 Pro Blue/Violet", "@drawable/vp45"))
        waxes.plusAssign(Wax("VP55 Pro Violet", "@drawable/vp55"))
        waxes.plusAssign(Wax("VP60 Pro Violet Red", "@drawable/vp60"))
        waxes.plusAssign(Wax("VP65 Pro Black Red", "@drawable/vp65"))
        waxes.plusAssign(Wax("V40LC Blue Grip Spray", "@drawable/v40lc"))
        waxes.plusAssign(Wax("V50LC Violet Grip Spray", "@drawable/v50lc"))
        waxes.plusAssign(Wax("V60LC Red Grip Spray", "@drawable/v60lc"))
        waxes.plusAssign(Wax("VR30 Lyse Blå", "@drawable/vr30"))
        waxes.plusAssign(Wax("VR40 Blå", "@drawable/vr40"))
        waxes.plusAssign(Wax("VR45 Flexi", "@drawable/vr45"))
        waxes.plusAssign(Wax("VR50 Fiolett", "@drawable/vr50"))
        waxes.plusAssign(Wax("VR55N Myk Fiolett", "@drawable/vr55n"))
        waxes.plusAssign(Wax("VR60 Sølv", "@drawable/vr60"))
        waxes.plusAssign(Wax("VR62 Hard Klistervoks", "@drawable/vr62"))
        waxes.plusAssign(Wax("VR65 Rød/Gul/Silver", "@drawable/vr65"))
        waxes.plusAssign(Wax("VR70 Rød Klistervoks", "@drawable/vr70"))
        waxes.plusAssign(Wax("VR75 Gul Klistervoks", "@drawable/vr75"))
        waxes.plusAssign(Wax("KX30 Blue Ice Klister", "@drawable/kx30"))
        waxes.plusAssign(Wax("KX35 Violet Special", "@drawable/kx35"))
        waxes.plusAssign(Wax("KN33 Nero Klister", "@drawable/kn33"))
        waxes.plusAssign(Wax("KX45 Violet Klister", "@drawable/kx45"))
        waxes.plusAssign(Wax("KX40S Silver Klister", "@drawable/kx40s"))
        waxes.plusAssign(Wax("KN44 Black Nero Klister", "@drawable/kn44"))
        waxes.plusAssign(Wax("KX65 Red Klister", "@drawable/kx65"))
        waxes.plusAssign(Wax("KX75 Red Extra Wet", "@drawable/kx75"))
        waxes.plusAssign(Wax("K22 Universal Klister", "@drawable/k22"))
    }

    //Sets the waxes that the user has chosen
    fun fillPersonalizedList() {
        personalizedWaxes = waxDao!!.getWaxes()
        Log.v("WaxRepo", "All wax $personalizedWaxes")
    }

    /* Based on name param gets the actual wax object */
    private fun getWaxObject(name : String) : Wax? {
        for (i in 0 until waxes.size) {
            if (name == waxes[i].name) {
                testObjekt = waxes[i]
                return waxes[i]
            }
        }
        return null
    }

    /*
    Fetches the best wax based on the weather and does not take in too
    consideration what the user has
     */
    fun getOptimalWax(temp: Float, snowType: String): Wax?  {
        val optimalWaxString = waxChooser.chooseOptimalWax(temp, snowType)
        return optimalWaxString?.let { getWaxObject(it) }
    }
    //Fetches the best wax based on the weather from the waxes that the user possess
    fun getPersonalWax(temp: Float, snowType: String): Pair<Wax?, String>{
        fillPersonalizedList()
        waxChooser.fillPersonalLists(personalWaxToString())
        val personalWaxString = waxChooser.choosePersonalWax(temp.roundToInt(), snowType)

        if(personalWaxString[0] == ""){
            return Pair(Wax(personalWaxString[1], "default_wax", false), "")
        }

        return Pair(getWaxObject(personalWaxString[0]), personalWaxString[1])
    }

    /* Parse the list of personal waxes into a list of the names*/
    private fun personalWaxToString(): MutableList<String?> {
        val stringList: MutableList<String?> = mutableListOf()
        for (element in personalizedWaxes) {
            stringList.add(element.name)
        }
        return stringList
    }


    /* Manual wax calculator based on userinput */
    fun getWaxFromManualCalculator(temp: Float, snowType: String, personal: Boolean): Pair<Wax, String> {
        waxCalculator.fillPersonalList(personalWaxToString())
        val waxString: Pair<String, String> =
                if (personal) waxCalculator.getPersonalWax(snowType, temp)
                else waxCalculator.getOptimalWax(snowType, temp)

        if(waxString.first == ""){
            return Pair(Wax(waxString.second, "default_wax", false), "")
        }
        return Pair(getWaxObject(waxString.first)!!, waxString.second)
    }

    fun addWax(wax: Wax){
        Log.v("WaxRepo", "Adding wax $wax")
        waxDao!!.addWax(wax)
        fillPersonalizedList()
    }
    fun removeWax(wax: Wax){
        Log.v("WaxRepo", "Removing wax $wax")
        waxDao!!.delete(wax)
        fillPersonalizedList()
    }

    fun clearData(){
        waxDao?.nukeTable()
        fillPersonalizedList()
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: WaxRepository? = null

        fun getInstance(waxDao: WaxDao) =
                instance ?: synchronized(this) {
                    instance ?: WaxRepository(waxDao).also { instance = it }
                }
        var testObjekt: Wax? = null
    }
}