package com.devour.reviewerapp.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    var map: HashMap<String, MutableList<String>> = HashMap()

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

        map= convertToHashMap(items)
        searchItemAdapter = SearchItemAdapter(items)
        itemSearchRv.adapter = searchItemAdapter

        return fragment
    }

    fun convertToHashMap(items: MutableList<Item>):HashMap<String,MutableList<String>>{
        val map : HashMap<String,MutableList<String>> = HashMap()
        for (item in items){
            val descList = item.desc.split("[\\\\s,.?:-]")
            map[item.title] = descList.toMutableList()
        }
        return map

    }



}