package com.example.skismoring.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.skismoring.R
import com.example.skismoring.ui.common.AlertDialogFragment
import com.example.skismoring.ui.common.DeleteAlertDialogFragment
import com.example.skismoring.ui.tips.TipsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsFragment : Fragment() {
    val nightSwitch: SwitchCompat? = null
    val contrastSwitch: SwitchCompat? = null

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsViewModel = ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("Theme", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val nightSwitch = view.findViewById<SwitchMaterial>(R.id.nightmodeSwitch)
        val contrastSwitch = view.findViewById<SwitchMaterial>(R.id.contrastSwitch)

        val oldThemeId = sharedPref.getInt("Theme", R.style.Theme_Skismoring_Light)
        val darkModeThemeId = R.style.Theme_Skismoring_Dark
        val highContrastThemeId = R.style.Theme_Skismoring_Contrast
        val normalModeThemeId = R.style.Theme_Skismoring_Light

        if (darkModeThemeId == oldThemeId) {
            nightSwitch.isChecked = true
            contrastSwitch.isChecked = false
        } else if (highContrastThemeId == oldThemeId) {
            nightSwitch.isChecked = false
            contrastSwitch.isChecked = true
        } else {
            nightSwitch.isChecked = false
            contrastSwitch.isChecked = false
        }

        // Handle navigate back
        val backButton = view.findViewById<ImageButton>(R.id.prev)
        backButton.setOnClickListener{
            findNavController().popBackStack()
        }

        // Changes the app to nightmode
        nightSwitch.setOnClickListener{
            val currentThemeId = sharedPref.getInt("Theme", normalModeThemeId)
            if (nightSwitch.isChecked) {
                contrastSwitch.isChecked = false
                // If the switch button is on
                editor.putInt("Theme", darkModeThemeId)
                editor.apply()
                if (darkModeThemeId != currentThemeId) requireActivity().recreate()
            } else {
                // If the switch button is off
                editor.putInt("Theme", normalModeThemeId)
                editor.apply()
                if (normalModeThemeId != currentThemeId) requireActivity().recreate()
            }
        }

        // Changes the app to have high contrast
        contrastSwitch.setOnClickListener{
            val currentThemeId = sharedPref.getInt("Theme", normalModeThemeId)
            if (contrastSwitch.isChecked) {
                nightSwitch.isChecked = false
                // If the switch button is on
                editor.putInt("Theme", highContrastThemeId)
                editor.apply()
                if (highContrastThemeId != currentThemeId) requireActivity().recreate()
            } else {
                // If the switch button is off
                editor.putInt("Theme", normalModeThemeId)
                editor.apply()
                if (normalModeThemeId != currentThemeId) requireActivity().recreate()
            }
        }

        // Delete all data
        val deleteButton = view.findViewById<ImageButton>(R.id.buttonClearData)
        deleteButton.setOnClickListener {
            val newFragment = DeleteAlertDialogFragment(settingsViewModel)
            newFragment.show(childFragmentManager, "infoWax")
        }

        // Go to WaxGarage
        val waxButton = view.findViewById<Button>(R.id.buttonTilBod)
        waxButton.setOnClickListener {
            findNavController().navigate(R.id.settingsToLube)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        contrastSwitch?.isChecked = false
        nightSwitch?.isChecked = false
    }
}