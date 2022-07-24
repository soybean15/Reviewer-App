package com.devour.reviewerapp.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.model.Item

interface SearchFragmentListener{
    fun onGetAllItems():MutableList<Item>


}
class SearchFragment(listener: SearchFragmentListener) : Fragment() {

    lateinit var searchItemAdapter: SearchItemAdapter
    var items = mutableListOf<Item>()
    val caller = listener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragment =  inflater.inflate(R.layout.fragment_search, container, false)

        val itemSearchRv:RecyclerView = fragment.findViewById(R.id.itemSearchRv)

        val linearLayout = LinearLayoutManager(context)
        val layoutManager: RecyclerView.LayoutManager = linearLayout
        itemSearchRv.layoutManager = layoutManager

        items = caller.onGetAllItems()


        searchItemAdapter = SearchItemAdapter(items)
        itemSearchRv.adapter = searchItemAdapter

        return fragment
    }




}