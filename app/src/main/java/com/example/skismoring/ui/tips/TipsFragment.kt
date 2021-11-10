package com.example.skismoring.ui.tips

import android.content.Context
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.skismoring.R
import com.example.skismoring.databinding.FragmentTipsBinding
import com.google.android.material.snackbar.Snackbar

class TipsFragment : Fragment() {
    private lateinit var tipsViewModel : TipsViewModel
    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!

    private var snowtype = "Tørr nyfallen"
    private var tempValues: Array<String> = arrayOf()
    private var buttonArray: Array<Pair<ImageButton, String>> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tipsViewModel = ViewModelProvider(requireActivity()).get(TipsViewModel::class.java)
        tempValues = resources.getStringArray(R.array.temperature_values)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        binding.apply {
            this.lifecycleOwner = this@TipsFragment
            this.viewmodel = tipsViewModel
        }
        //
        buttonArray = arrayOf(
                Pair(binding.snowtypeDryNewlyFallen, getString(R.string.dryNewlyFallen)),
                Pair(binding.snowtypeDryFineGrained, getString(R.string.dryFineGrained)),
                Pair(binding.snowtypeDryOldConverted, getString(R.string.dryOldConverted)),
                Pair(binding.snowtypeDryCoarseGrained, getString(R.string.dryCoarseGrained)),
                Pair(binding.snowtypeWetNewlyFallen, getString(R.string.wetNewlyFallen)),
                Pair(binding.snowtypeWetFineGrained, getString(R.string.wetFineGrained)),
                Pair(binding.snowtypeWetOldConverted, getString(R.string.wetOldConverted)),
                Pair(binding.snowtypeWetCoarseGrained, getString(R.string.wetCoarseGrained)),
        )
        setInitialStateOfView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSnowTypeClickListeners()

        // Setup the number picker
        binding.temperature.minValue  = 0
        binding.temperature.maxValue = tempValues.size - 1
        binding.temperature.value = tempValues.size / 2
        binding.temperature.displayedValues = tempValues
        binding.temperature.wrapSelectorWheel = false
        binding.temperature.setOnValueChangedListener { _, _, newVal ->
            getRecommendation(tempValues[newVal], snowtype)
        }

        // Optimal recommendation
        val optimalImage: ImageView = binding.recommendation.findViewById(R.id.optimalWaxImage) as ImageView
        val optimalText: TextView = binding.recommendation.findViewById(R.id.optimalWaxName) as TextView
        tipsViewModel.optimalRecommendation.observe(viewLifecycleOwner, {
            if (it.wax != null){
                optimalText.text = it.wax.name
                val context: Context = requireContext()
                Glide.with(context)
                        .load(context.resources.getIdentifier(it.wax.img, "drawable", context.packageName))
                        .into(optimalImage)
            }
        })

        // Personal recommendation
        val personalImage: ImageView = binding.recommendation.findViewById(R.id.personalWaxImage) as ImageView
        val personalText: TextView = binding.recommendation.findViewById(R.id.personalWaxName) as TextView
        val accuracy: TextView = binding.recommendation.findViewById(R.id.accuracy) as TextView
        val wax: ImageButton = binding.recommendation.findViewById(R.id.waxButton)
        wax.setOnClickListener {
            findNavController().navigate(R.id.tipsToWax)
        }
        tipsViewModel.personalRecommendation.observe(viewLifecycleOwner, {
            if (it.wax != null) {
                personalText.text = it.wax.name
                accuracy.text = it.precision
                val context: Context = requireContext()
                Glide.with(context)
                        .load(context.resources.getIdentifier(it.wax.img, "drawable", context.packageName))
                        .into(personalImage)
            }
        })
    }

    private fun getRecommendation(temperature: String, snowtype: String) {
        tipsViewModel.getTips(temperature, snowtype)
    }

    private fun setInitialStateOfView(){
        for (button in buttonArray){
            button.first.alpha = 0.5F
        }
        buttonArray[0].first.alpha = 1.0F
    }

    /* Set onclick for all buttons that also produce the snackbar to
    *  show user which snowtype the button corresponds to*/
    private fun setSnowTypeClickListeners(){
        for (button in buttonArray){
            button.first.setOnClickListener {
                it.alpha = 1.0F
                for (otherButton in buttonArray){
                    if (otherButton.first != it) otherButton.first.alpha = 0.5F
                }
                snowtype = button.second
                getRecommendation(tempValues[binding.temperature.value], snowtype)
                Snackbar.make(binding.root, "Valgt snøtype: ${button.second}", 1000)
                        .setAnchorView(requireView().rootView.findViewById(R.id.bottom_nav_view))
                        .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::tipsViewModel.isInitialized){
            tipsViewModel.getTips(tempValues[binding.temperature.value], snowtype)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
