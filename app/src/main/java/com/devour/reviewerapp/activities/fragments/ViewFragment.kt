package com.devour.reviewerapp.activities.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.data.data_source.AppData
import com.devour.reviewerapp.data.relationship.TermWithTopics
import com.devour.reviewerapp.data.relationship.TopicWithItems
import com.devour.reviewerapp.model.Item
import com.devour.reviewerapp.model.Term
import com.devour.reviewerapp.model.Topic


interface ViewFragmentListener{
    fun getItemWithoutTerm():MutableList<Item>
    fun getItemWithoutTopic():MutableList<Item>
    fun getTermsWithTopics():MutableList<TermWithTopics>

}

class ViewFragment (): Fragment() {

    lateinit var termsWithTopicAdapter: TermsWithTopicAdapter
    lateinit var caller: ViewFragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ViewFragmentListener) {
            caller = context
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragment = inflater.inflate(R.layout.fragment_view, container, false)

        val topicWithTermsRv:RecyclerView = fragment.findViewById(R.id.termsWithTopicRv)
        val context = activity as Context



        val linearLayout = LinearLayoutManager(context)
        val layoutManager: RecyclerView.LayoutManager = linearLayout

        topicWithTermsRv.layoutManager = layoutManager
        val termsWithTopics = caller.getTermsWithTopics()

        val _termsWithTopics = termsWithTopics.toMutableList()
        Log.i("SIzeLog", "Size of inside fragment ${termsWithTopics.size}")
        addItemsWithoutTerm(_termsWithTopics)


        termsWithTopicAdapter = TermsWithTopicAdapter(_termsWithTopics,caller)
        topicWithTermsRv.adapter = termsWithTopicAdapter


        return fragment
    }



    fun addItemsWithoutTerm(termsWithTopics: MutableList<TermWithTopics>){

        val itemsWithoutTerms =caller.getItemWithoutTerm()

        if (!itemsWithoutTerms.isEmpty()){
            Log.i("SIzeLog", "Size ${itemsWithoutTerms.size}")
            termsWithTopics.add(
                TermWithTopics(
                    Term("Untitled",-1,System.currentTimeMillis(), Color.BLACK),
                    mutableListOf(
                        TopicWithItems(
                            Topic("Untitled",-1,-1,System.currentTimeMillis(), Color.BLACK),
                            itemsWithoutTerms
                        )
                    )
                )
            )
        }



    }


}