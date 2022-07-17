package com.devour.reviewerapp.activities.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.data.relationship.TermWithTopics

class TermsWithTopicAdapter(var termsWithTopic:  MutableList<TermWithTopics> ) :
    RecyclerView.Adapter<TermsWithTopicViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermsWithTopicViewHolder {
        return TermsWithTopicViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_view_term_with_topic, parent, false)
        )

    }

    override fun onBindViewHolder(holder: TermsWithTopicViewHolder, position: Int) {
       val termTitleTextView:TextView = holder.itemView.findViewById(R.id.topicTitleTextView)

        val topicsWithItemRv:RecyclerView = holder.itemView.findViewById(R.id.topicsWithItemRv)
        val linearLayout = LinearLayoutManager(holder.itemView.context)
        val layoutManager: RecyclerView.LayoutManager = linearLayout
        topicsWithItemRv.layoutManager = layoutManager

        termTitleTextView.text = termsWithTopic[position].term.name

        val topicsWithItemsAdapter = TopicWithItemsAdapter(termsWithTopic[position].topicWithItems)
        topicsWithItemRv.adapter = topicsWithItemsAdapter

    }

    override fun getItemCount(): Int {
        return termsWithTopic.size
    }

}



class TermsWithTopicViewHolder(inflater: View) : RecyclerView.ViewHolder(inflater)
