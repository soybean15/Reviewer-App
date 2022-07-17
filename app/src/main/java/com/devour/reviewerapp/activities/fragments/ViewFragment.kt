package com.devour.reviewerapp.activities.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.data.relationship.TermWithTopics


class ViewFragment (val termsWithTopics: MutableList<TermWithTopics>): Fragment() {

    lateinit var termsWithTopicAdapter: TermsWithTopicAdapter

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
        termsWithTopicAdapter = TermsWithTopicAdapter(termsWithTopics)
        topicWithTermsRv.adapter = termsWithTopicAdapter


        return fragment
    }

    companion object {

        fun newInstance(termsWithTopics:  MutableList<TermWithTopics>) :ViewFragment{
            return ViewFragment(termsWithTopics)
        }
    }
}