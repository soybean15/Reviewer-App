package com.devour.reviewerapp.activities.fragments


import android.graphics.Color
import android.util.Log
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
import com.devour.reviewerapp.model.Term
import com.devour.reviewerapp.model.Topic


class TermsWithTopicAdapter(var termsWithTopic:  MutableList<TermWithTopics> , viewFragmentListener: ViewFragmentListener) :
    RecyclerView.Adapter<TermsWithTopicViewHolder>() {

    private val caller= viewFragmentListener

    fun addItemsWithoutTopic(topicWithItems:  MutableList<TopicWithItems>, termId: Int){

        val itemsWithoutTopic =caller.getItemWithoutTopic()

        if (!itemsWithoutTopic.isEmpty()){
            if(itemsWithoutTopic[0].termId == termId){
                Log.i("SIzeLogz", "Size ${itemsWithoutTopic.size}")

                topicWithItems.add(
                    TopicWithItems(
                        Topic("Untitled",-1,-1,System.currentTimeMillis(), Color.BLACK),
                        itemsWithoutTopic
                    )
                )
            }



        }



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermsWithTopicViewHolder {

        Log.i("CheckTag", "Size ${termsWithTopic.size}")
        return TermsWithTopicViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_view_term_with_topic, parent, false)
        )

    }

    override fun onBindViewHolder(holder: TermsWithTopicViewHolder, position: Int) {




        val topicsWithItemRv:RecyclerView = holder.itemView.findViewById(R.id.topicsWithItemRv)
        val termDropdown:View = holder.itemView.findViewById(R.id.termDropdown)

        holder.bind(termsWithTopic[position])

        val _topicWithItems = termsWithTopic[position].topicWithItems.toMutableList()


        addItemsWithoutTopic(_topicWithItems,termsWithTopic[position].term.termId)

        val linearLayout = LinearLayoutManager(holder.itemView.context)
        val layoutManager: RecyclerView.LayoutManager = linearLayout
        topicsWithItemRv.layoutManager = layoutManager
        val topicsWithItemsAdapter = TopicWithItemsAdapter(_topicWithItems)
        topicsWithItemRv.adapter = topicsWithItemsAdapter

        if (_topicWithItems.isNotEmpty()) {

            var isHidden = true
            topicsWithItemRv.visibility = View.GONE

            termDropdown.background = ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.ic_baseline_arrow_drop_down_24
            )
            holder.itemView.setOnClickListener {
                if (!isHidden) {
                    termDropdown.background = ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.ic_baseline_arrow_drop_down_24
                    )
                    topicsWithItemRv.visibility = View.GONE
                    isHidden = true
                } else {
                    termDropdown.background = ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.ic_baseline_arrow_drop_up_24
                    )
                    topicsWithItemRv.visibility = View.VISIBLE
                    isHidden = false
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return termsWithTopic.size
    }

}



class TermsWithTopicViewHolder(inflater: View) : RecyclerView.ViewHolder(inflater){

    private var termTitleTextView:TextView? = null
    init {

         termTitleTextView = itemView.findViewById(R.id.topicTitleTextView)

    }

    fun bind(termsWithTopic: TermWithTopics){
        termTitleTextView!!.text = termsWithTopic.term.name
    }


}
