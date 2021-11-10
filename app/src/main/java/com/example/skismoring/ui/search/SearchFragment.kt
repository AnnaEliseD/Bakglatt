package com.example.skismoring.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skismoring.R
import com.example.skismoring.data.model.Location
import com.example.skismoring.ui.map.MapViewModel
import com.example.skismoring.ui.map.Position

class SearchFragment : Fragment(), SearchAdapter.OnItemClickListener {

    private lateinit var mapViewModel : MapViewModel
    private lateinit var searchViewModel: SearchViewModel
    private var searchAdapter = SearchAdapter(arrayListOf(), true, this)

    private lateinit var searchInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapViewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        searchViewModel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)
        searchViewModel.readData(requireContext())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle navigate back
        val backButton = view.findViewById<ImageButton>(R.id.prev)
        backButton.setOnClickListener{
            findNavController().popBackStack()
        }

        // Search input
        val input = view.findViewById<EditText>(R.id.search_input)
        // Autofocus and get keyboard
        input.requestFocusFromTouch()
        val imm = input.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)

        // Handle search filtering
        input.addTextChangedListener(object : TextWatcher {
            //Due to interface, these functions were implemented, but they don't deliver any functionality to our application
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchViewModel.filter(s.toString())
            }
        })

        // Search results
        searchInfo = view.findViewById(R.id.search_info)
        val searchRecyclerView = view.findViewById<RecyclerView>(R.id.searchList)
        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        searchRecyclerView.adapter = searchAdapter
        searchViewModel.searchListFiltered.observe(viewLifecycleOwner, {
            searchAdapter.filterList(it)
            searchAdapter.notifyDataSetChanged()

            searchInfo.isVisible = it.isEmpty()
        })

    }

    override fun onItemClick(item: Location) {
        val newPos = Position(item.lat, item.lng, 15.0)
        mapViewModel.changePos(newPos)
        findNavController().popBackStack()
    }

    override fun onResume() {
        super.onResume()
        searchInfo.isVisible = false
    }
}


