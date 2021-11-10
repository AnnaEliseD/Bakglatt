package com.example.skismoring.ui.onBoarding.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.skismoring.R

class ThirdScreen : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_third_screen, container, false)

        view.findViewById<ImageButton>(R.id.include).setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_bottom_nav)
            onBoardingFinished()
        }

        return view
    }

    /* Checks if onBoarding has been done,
     * if true the application will take the user directyl to the mapView */
    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("onBoarding", true)
        editor.apply()
    }
}