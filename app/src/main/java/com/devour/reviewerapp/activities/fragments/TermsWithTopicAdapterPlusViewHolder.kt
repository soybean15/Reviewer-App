package com.devour.reviewerapp.activities.fragments


import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    fun addItemsWithoutTopic(topicWithItems:  MutableList<TopicWithItems>){

        val itemsWithoutTopic =caller.getItemWithoutTopic()

        if (!itemsWithoutTopic.isEmpty()){
            Log.i("SIzeLogz", "Size ${itemsWithoutTopic.size}")

                    topicWithItems.add(
                        TopicWithItems(
                            Topic("Untitled",-1,-1,System.currentTimeMillis(), Color.BLACK),
                            itemsWithoutTopic
                        )
                    )


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





        var isHidden = true

        val topicsWithItemRv:RecyclerView = holder.itemView.findViewById(R.id.topicsWithItemRv)
        val linearLayout = LinearLayoutManager(holder.itemView.context)
        val layoutManager: RecyclerView.LayoutManager = linearLayout
        topicsWithItemRv.layoutManager = layoutManager
        topicsWithItemRv.visibility= View.GONE


        holder.bind(termsWithTopic[position])

        val _topicWithItems = termsWithTopic[position].topicWithItems.toMutableList()

        addItemsWithoutTopic(_topicWithItems)

        val topicsWithItemsAdapter = TopicWithItemsAdapter(_topicWithItems)
        topicsWithItemRv.adapter = topicsWithItemsAdapter

        holder.itemView.setOnClickListener {
            if(!isHidden){
                topicsWithItemRv.visibility= View.GONE
                isHidden=  true
            }else{
                topicsWithItemRv.visibility= View.VISIBLE
                isHidden=  false
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
