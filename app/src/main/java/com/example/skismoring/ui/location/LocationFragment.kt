package com.example.skismoring.ui.location

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.skismoring.R
import com.example.skismoring.databinding.FragmentLocationBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationFragment : Fragment() {

    private lateinit var viewModel: LocationViewModel
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        binding.apply {
            this.lifecycleOwner = this@LocationFragment
            this.viewmodel = viewModel
        }

        return binding.root
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        // Progress bar
        binding.progressOverlay.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        viewModel.loading.observe(viewLifecycleOwner,{
            if (it) binding.progressOverlay.visibility = View.VISIBLE else binding.progressOverlay.visibility = View.GONE
        })

        // Back button
        val backButton = root.findViewById<ImageButton>(R.id.prev)
        backButton.setOnClickListener{
            findNavController().popBackStack()
        }

        // Handle content
        viewModel.currentLocation.observe(viewLifecycleOwner, {
            binding.title.text = it.name
            if (it.favorite) {
                binding.favoriteButton.setImageResource(R.drawable.ic_baseline_star_24)
            } else {
                binding.favoriteButton.setImageResource(R.drawable.ic_baseline_star_outline_24)
            }
        })

        // Icon
        val sharedPref = requireActivity().getSharedPreferences("Theme", Context.MODE_PRIVATE)
        when (sharedPref.getInt("Theme", R.style.Theme_Skismoring_Light)) {
            R.style.Theme_Skismoring_Light -> binding.weatherIcon.alpha = 1.0F
            R.style.Theme_Skismoring_Dark -> binding.weatherIcon.alpha = 0.5F
            R.style.Theme_Skismoring_Contrast -> binding.weatherIcon.alpha = 1.0F
        }

        // Optimal reccomendation
        val optimalImage: ImageView =
            binding.recommendation.findViewById(R.id.optimalWaxImage) as ImageView
        val optimalText: TextView =
            binding.recommendation.findViewById(R.id.optimalWaxName) as TextView
        viewModel.optimalRecommendation.observe(viewLifecycleOwner, {
            if (it.wax != null) {
                optimalText.text = it.wax.name
                val context: Context = requireContext()
                Glide.with(context)
                    .load(
                        context.resources.getIdentifier(
                            it.wax.img,
                            "drawable",
                            context.packageName
                        )
                    )
                    .into(optimalImage)
            }
        })

        // Personal reccomendation
        val personalImage: ImageView =
            binding.recommendation.findViewById(R.id.personalWaxImage) as ImageView
        val personalText: TextView =
            binding.recommendation.findViewById(R.id.personalWaxName) as TextView
        val accuracy: TextView = binding.recommendation.findViewById(R.id.accuracy) as TextView
        val wax: ImageButton = binding.recommendation.findViewById(R.id.waxButton)
        wax.setOnClickListener {
            findNavController().navigate(R.id.locationToWax)
        }
        viewModel.personalRecommendation.observe(viewLifecycleOwner, {
            if (it.wax != null) {
                personalText.text = it.wax.name
                accuracy.text = it.precision
                val context: Context = requireContext()
                Glide.with(context)
                    .load(
                        context.resources.getIdentifier(
                            it.wax.img,
                            "drawable",
                            context.packageName
                        )
                    )
                    .into(personalImage)
            }
        })
        // Weather
        viewModel.weather.observe(viewLifecycleOwner, {
            Log.v("icon", it.second)
            val context: Context = requireContext()
            Glide.with(context)
                .load(context.resources.getIdentifier(it.second, "drawable", context.packageName))
                .into(binding.weatherIcon)
        })

        // SnackBar
        binding.favoriteButton.setOnClickListener {

            viewModel.updateCurrentLocationFavorite()

            if (viewModel.currentLocation.value!!.favorite) {

                Snackbar.make(binding.root,R.string.addFavorites, Snackbar.LENGTH_SHORT)
                        .setAnchorView(requireView().rootView.findViewById(R.id.bottom_nav_view))
                        .show()

            } else {
                Snackbar.make(binding.root,R.string.removeFavorites, Snackbar.LENGTH_SHORT)
                        .setAnchorView(requireView().rootView.findViewById(R.id.bottom_nav_view))
                        .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.updateFavorites()

        val favorite = viewModel.currentLocation.value?.favorite ?: false
        Log.v("locfrag", favorite.toString())
        if (favorite) {
            binding.favoriteButton.setImageResource(R.drawable.ic_baseline_star_24)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.ic_baseline_star_outline_24)
        }

        // Make the location register changes in wax garage
        CoroutineScope(Dispatchers.IO).launch{
            delay(10)
            viewModel.setCurrentLocation(viewModel.currentLocation.value!!)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

