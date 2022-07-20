package com.devour.reviewerapp.activities.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.data.relationship.TermWithTopics
import com.devour.reviewerapp.data.relationship.TopicWithItems
import com.devour.reviewerapp.model.Item

class TopicWithItemsAdapter(var topicWithItems: MutableList<TopicWithItems> ) :
    RecyclerView.Adapter<TopicWithItemsViewHolder>() {


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
        val topicDropDown:View = holder.itemView.findViewById(R.id.topicDropdown)

        val linearLayout = LinearLayoutManager(holder.itemView.context)
        val layoutManager: RecyclerView.LayoutManager = linearLayout
        itemRecyclerView.layoutManager = layoutManager



        val itemAdapter = ItemAdapter(topicWithItems[position].items)
        itemRecyclerView.adapter = itemAdapter


        if(topicWithItems[position].items.isNotEmpty()){
            var isHidden = true
            itemRecyclerView.visibility= View.GONE
            topicDropDown.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_baseline_arrow_drop_down_24)
            holder.itemView.setOnClickListener {
                if(!isHidden){
                    topicDropDown.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_baseline_arrow_drop_down_24)
                    itemRecyclerView.visibility= View.GONE
                    isHidden=  true
                }else{
                    topicDropDown.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_baseline_arrow_drop_up_24)
                    itemRecyclerView.visibility= View.VISIBLE
                    isHidden=  false
                }
            }

        }


    }

}



class TopicWithItemsViewHolder(inflater: View) : RecyclerView.ViewHolder(inflater)
