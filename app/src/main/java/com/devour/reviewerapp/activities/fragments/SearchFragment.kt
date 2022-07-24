package com.devour.reviewerapp.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
            val descList = item.desc.lowercase().split("[\\\\s,.?:-]")
            map[item.title] = descList.toMutableList()
        }
        return map

    }
    fun findItem(fragment: View){
        val searchFragmentEditText:EditText = fragment.findViewById(R.id.searchFragmentEditText)
        val searchFragmentButton:Button = fragment.findViewById(R.id.searchFragmentButton)
        searchFragmentButton.setOnClickListener {
            val desc = searchFragmentButton.text.toString().lowercase().split("[\\\\s,.?:-]")
        }


    }

    fun findTopAnswer(desc:List<String>): HashMap<Int, String>{
        val bestAnswer: HashMap<Int, String> = HashMap()

        for (key in map.keys) {
            val score: Int = searchProbability(key, desc)
            if (score != 0) {
                bestAnswer[score] = key
            }
        }
        return bestAnswer
    }

   fun searchProbability(key:String, desc:List<String>):Int{
       var rating = 0

       for(str in desc){
           if(map[key]!!.contains(str)){
               rating+=5
           }
       }
       return rating

   }





}