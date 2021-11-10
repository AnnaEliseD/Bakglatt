package com.example.skismoring.ui.favorites

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skismoring.R
import com.example.skismoring.data.manager.LocationRepository
import com.example.skismoring.ui.location.LocationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text

class FavoritesFragment : Fragment(), FavoritesAdapter.OnRecyclerViewItemClickListener {
    private lateinit var rv: RecyclerView
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var locationViewModel: LocationViewModel


    private var adapter = FavoritesAdapter(listOf(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoritesViewModel = ViewModelProvider(requireActivity()).get(FavoritesViewModel::class.java)
        locationViewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        rv = view.findViewById(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = adapter
        favoritesViewModel.updateFavorites()


        /*
        Checks if the database with favorites has changed and if so it updates the list with
        favorites in the fragment
         */
        favoritesViewModel.favorites.observe(viewLifecycleOwner, { locations ->
            Log.v("favAdapter", "Locations: $locations")
            adapter.setData(locations)
            adapter.notifyDataSetChanged()
            rv.isVisible = locations.isNotEmpty()

            val favInfo: TextView = view.findViewById(R.id.favorite_info)
            favInfo.isVisible = locations.isEmpty()


        })
    }

    /*
    Function that changes the fragment to a location fragment for the location the user clicked
    on. If the user clicks on the star the location will be removed from the list and the database.
    */
    override fun onRecyclerViewItemClicked(position: Int, id: Int) {

        val location = favoritesViewModel.favorites.value?.get(position)
        if (id==-1){
            if (location != null) {
                locationViewModel.setCurrentLocation(location)
            }
            findNavController().navigate(R.id.favoritesToLocation)
        }
        else{
            if (location != null){

                favoritesViewModel.deleteFavorite(location)

                Snackbar.make(rv, R.string.labelUndo, 3000)
                        .setAction(R.string.actionText) {
                            favoritesViewModel.addFavorite(location)
                        }
                        .setAnchorView(requireView().rootView.findViewById(R.id.bottom_nav_view))
                        .show()
            }
        }
    }
}