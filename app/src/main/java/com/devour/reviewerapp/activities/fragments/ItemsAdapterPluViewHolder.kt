package com.devour.reviewerapp.activities.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.activities.components.SyntaxHighlighter
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

        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

}



    class ItemViewHolder(inflater: View) : RecyclerView.ViewHolder(inflater){
        var title :TextView? = null
        var desc:TextView? =null
        init {
            title  = itemView.findViewById(R.id.itemTitle)
            desc = itemView.findViewById(R.id.itemDesc)



        }

        fun bind(item: Item){

            val highlight = SyntaxHighlighter.highlight()
            title!!.text = item.title


            val _desc = item.desc


            if(  _desc.endsWith("```")){


                desc!!.text =  _desc.dropLast(3)

                highlight.setSpan(desc)
            }else{
                desc!!.text = _desc
            }



        }
    }
