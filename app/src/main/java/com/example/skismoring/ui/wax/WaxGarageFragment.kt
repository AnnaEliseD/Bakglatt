package com.example.skismoring.ui.wax

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skismoring.R
import com.example.skismoring.data.model.Wax
import com.example.skismoring.ui.common.AlertDialogFragment

class WaxGarageFragment: Fragment(R.layout.fragment_wax), WaxAdapter.OnItemClickListener {

    private lateinit var rv: RecyclerView
    private lateinit var waxViewModel: WaxViewModel
    private lateinit var adapter: WaxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waxViewModel = ViewModelProvider(requireActivity()).get(WaxViewModel::class.java)
        waxViewModel.updateWaxes()
        adapter = WaxAdapter(waxViewModel.allWax, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wax, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv = view.findViewById(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = adapter

        // Waxes
        waxViewModel.waxes.observe(viewLifecycleOwner, {
            adapter.updatePersonalWaxes(it)
            adapter.notifyDataSetChanged()
        })

        // Back button
        val backButton = view.findViewById<ImageButton>(R.id.prev)
        backButton.setOnClickListener{
            findNavController().popBackStack()
        }

        // Info button
        val infoButton: ImageButton = view.findViewById(R.id.infoButton)
        infoButton.setOnClickListener{
            val newFragment = AlertDialogFragment()
            newFragment.show(childFragmentManager, "infoWax")
        }
    }

    /*
    If the user checks the box the wax is added to the database,
    when the user unchecks the wax is removed.
     */
    override fun onItemClick(wax: Wax, checked: Boolean) {
        if(checked) waxViewModel.addWax(wax) else waxViewModel.removeWax(wax)
    }

    override fun onResume() {
        super.onResume()
        if (this::waxViewModel.isInitialized) waxViewModel.updateWaxes()
    }
}