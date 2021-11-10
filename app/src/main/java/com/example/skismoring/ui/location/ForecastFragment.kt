package com.example.skismoring.ui.location

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skismoring.R

class ForecastFragment() : Fragment() {

    private lateinit var locViewModel: LocationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locViewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locViewModel.getWeatherForecast()
        locViewModel.wForecast.observe(viewLifecycleOwner, {
            val recyclerView: RecyclerView = view.findViewById(R.id.rv_forecast)
            recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = WeatherAdapter(it?.properties!!.timeseries.take(10))
            recyclerView.adapter = adapter
        })
    }
}