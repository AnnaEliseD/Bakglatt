package com.example.skismoring.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skismoring.R
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Heavy inspiration from: https://github.com/RafhaanShah/Android-Navigation-Samples
 */

class BottomNavFragment : Fragment() {

    private val bottomNavSelectedItemIdKey = "BOTTOM_NAV_SELECTED_ITEM_ID_KEY"
    private var bottomNavSelectedItemId = R.id.map // Must be your starting destination

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            bottomNavSelectedItemId =
                    savedInstanceState.getInt(bottomNavSelectedItemIdKey, bottomNavSelectedItemId)
        }
        setupBottomNavBar(view)
    }

    // Needed to maintain correct state over rotations
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(bottomNavSelectedItemIdKey, bottomNavSelectedItemId)
        super.onSaveInstanceState(outState)
    }

    private fun setupBottomNavBar(view: View) {
        val bottomNavView = view.findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        // Your navGraphIds must have the same ids as your menuItem ids
        val navGraphIds = listOf(
                R.navigation.map,
                R.navigation.favorites,
                R.navigation.tips
                )

        bottomNavView.selectedItemId = bottomNavSelectedItemId // Needed to maintain correct state on return

        val controller = bottomNavView.setupWithNavController(
                fragmentManager = childFragmentManager,
                navGraphIds = navGraphIds,
                containerId = R.id.bottom_nav_container,
                firstItemId = R.id.map, // Must be the same as bottomNavSelectedItemId
                intent = requireActivity().intent
        )

        controller.observe(viewLifecycleOwner, { navController ->
            bottomNavSelectedItemId = navController.graph.id // Needed to maintain correct state on return
        })
    }
}