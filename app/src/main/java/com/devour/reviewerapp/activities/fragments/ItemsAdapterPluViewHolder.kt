package com.devour.reviewerapp.activities.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.model.Item

class ItemAdapter(var items :MutableList<Item>):
    RecyclerView.Adapter<ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_view_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val title :TextView = holder.itemView.findViewById(R.id.itemTitle)
        val desc:TextView = holder.itemView.findViewById(R.id.itemDesc)

        title.text = items[position].title
        desc.text = items[position].desc
    }

    override fun getItemCount(): Int {
        return items.size
    }

}



    class ItemViewHolder(inflater: View) : RecyclerView.ViewHolder(inflater)
