package com.devour.reviewerapp.activities.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.data.relationship.TermWithTopics
import com.devour.reviewerapp.data.relationship.TopicWithItems
import com.devour.reviewerapp.model.Item

class TopicWithItemsAdapter(var topicWithItems: MutableList<TopicWithItems> ) :
    RecyclerView.Adapter<TopicWithItemsViewHolder>() {

    fun getItemWithoutTerms(termsWithTopics: MutableList<TermWithTopics>):MutableList<Item>{
        val items:MutableList<Item> = mutableListOf()

        for (termWithTopics in termsWithTopics) {

            for (topicWithItems in termWithTopics.topicWithItems){
                for (item in topicWithItems.items ){
                    if (item.termId <0){
                        items.add(item)
                    }
                }
            }
        }

        return items

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicWithItemsViewHolder {

        return TopicWithItemsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_view_topic_with_item, parent, false)
        )

    }


    override fun getItemCount(): Int {
        return topicWithItems.size
    }

    override fun onBindViewHolder(holder: TopicWithItemsViewHolder, position: Int) {
        val topicTitleTextView: TextView = holder.itemView.findViewById(R.id.topicTitleTextView)
        topicTitleTextView.text = topicWithItems[position].topic.title

        val itemRecyclerView:RecyclerView = holder.itemView.findViewById(R.id.itemRecyclerView)
        val linearLayout = LinearLayoutManager(holder.itemView.context)
        val layoutManager: RecyclerView.LayoutManager = linearLayout
        itemRecyclerView.layoutManager = layoutManager



        val itemAdapter = ItemAdapter(topicWithItems[position].items)
        itemRecyclerView.adapter = itemAdapter
    }

}



class TopicWithItemsViewHolder(inflater: View) : RecyclerView.ViewHolder(inflater)
