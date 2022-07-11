package com.devour.reviewerapp.activities.fragments

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.activities.components.SubjectViewHolder
import com.devour.reviewerapp.data.relationship.TermWithTopics
import com.devour.reviewerapp.data.relationship.TopicWithItems
import com.devour.reviewerapp.model.Item
import com.devour.reviewerapp.model.Subject


interface AddFragmentListener {
    fun addItems(title: String, desc: String): MutableList<Item>
    fun loadTermSpinner(): MutableList<TermWithTopics>
    fun loadTopicSpinner(): MutableList<TopicWithItems>
    fun onAddTermClick()
    fun onAddTopicClick()
    fun getTermPos(): Int

    fun retrieveAllItems():MutableList<Item>

    fun onTermSelectedItem(position: Int)
    fun onTopicSelectedItem(position: Int)

}

class AddFragment() : Fragment() {

    lateinit var caller: AddFragmentListener

    private var spinnerInitialization = false
    private var _spinnerInitialization = false
    var topicWithItems:MutableList<TopicWithItems> ?= null
    private var termWithTopics :MutableList<TermWithTopics>?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddFragmentListener) {
            caller = context
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragment = inflater.inflate(R.layout.fragment_add, container, false)


        val addItemRecyclerView: RecyclerView = fragment.findViewById(R.id.AddItemRecyclerView)


        val context = activity as Context




         loadTermSpinner(context, fragment)

        loadTopicSpinner( context, fragment)


        loadComponents(context, fragment)
        val items = caller.retrieveAllItems()

        val linearLayout = LinearLayoutManager(context)
        linearLayout.stackFromEnd =true
        linearLayout.reverseLayout = false

        val layoutManager: RecyclerView.LayoutManager = linearLayout

        addItemRecyclerView.layoutManager = layoutManager

         addItemRecyclerView.adapter =AddItemAdapter(items)


        return fragment
    }

    companion object {
        fun newInstance(): AddFragment {
            return AddFragment()
        }
    }


    //
//    private fun addItem(recyclerView: RecyclerView,fragment: View){
//        val titleEditTextView:EditText = fragment.findViewById(R.id.titleEditTextView)
//        val descEditTextView:EditText = fragment.findViewById(R.id.descriptionEditTextView)
//        val addSaveButton:Button = fragment.findViewById(R.id.addSaveButton)
//        addSaveButton.setOnClickListener {
//            val title = titleEditTextView.text.toString()
//            val desc = descEditTextView.text.toString()
//
//            val items = caller.addItems(title,desc)
//
//            recyclerView.adapter =AddItemAdapter(items)
//            titleEditTextView.text.clear()
//            descEditTextView.text.clear()
//
//        }
//    }
//
//

    private fun  extractTermTitle(termWithTopics:MutableList<TermWithTopics>):MutableList<String>{


        val items = mutableListOf<String>()
        for(item in termWithTopics ){
            items.add(item.term.name)
        }
        return items
    }

    private fun  extractTopicTitle(topicWithItems:MutableList<TopicWithItems>):MutableList<String>{


        val items = mutableListOf<String>()
        for(item in topicWithItems ){
            items.add(item.topic.title)
        }
        return items
    }




    private fun loadTermSpinner(context: Context, fragment: View){
        val termsSpinner: Spinner = fragment.findViewById(R.id.termSpinner)



         termWithTopics =  caller.loadTermSpinner()


        val terms =extractTermTitle(termWithTopics!!)
        val position = caller.getTermPos()
        if(position>=0){
            topicWithItems = termWithTopics!![position].topicWithItems
        }


        termsSpinner.setSelection(position)


        val aa = ArrayAdapter(
            context,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            terms
        )
        aa.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)

        aa.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)



        termsSpinner.adapter = aa
        if (position >= 0) {

            termsSpinner.post(Runnable {
                kotlin.run {
                    termsSpinner.setSelection(position)
                }
            })

        }



    }

    private fun loadTopicSpinner(context: Context, fragment: View){

        if(topicWithItems != null){
            val topicSpinner: Spinner = fragment.findViewById(R.id.topicSpinner)



            val topics =extractTopicTitle(topicWithItems!!)


            val aa = ArrayAdapter(
                context,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                topics
            )
            aa.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
            aa.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)


            topicSpinner.adapter = aa
        }


    }


    private fun loadComponents(context: Context, fragment: View) {
        val addTermButton: Button = fragment.findViewById(R.id.addTermButton)
        val addTopicButton: Button = fragment.findViewById(R.id.addTopicButton)

        addTermButton.setOnClickListener {
            caller.onAddTermClick()


        }

        addTopicButton.setOnClickListener {
            caller.onAddTopicClick()

        }

        val termsSpinner: Spinner = fragment.findViewById(R.id.termSpinner)
        val topicSpinner: Spinner = fragment.findViewById(R.id.topicSpinner)

        termsSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position:  Int, p3: Long) {

                if(!spinnerInitialization){
                    spinnerInitialization =true
                    return
                }
                caller.onTermSelectedItem(position)
                topicWithItems = termWithTopics!![position].topicWithItems
                loadTopicSpinner( context, fragment)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                return
            }

        })


        topicSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position:  Int, p3: Long) {

                if(!_spinnerInitialization){
                    _spinnerInitialization =true
                    return
                }
                caller.onTopicSelectedItem(position)

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                return
            }

        })


        val addItemButton:Button = fragment.findViewById(R.id.addItemButton)
        val titleEditTextView :EditText = fragment.findViewById(R.id.titleEditTextView)
        val descriptionEditTextView :EditText = fragment.findViewById(R.id.descriptionEditTextView)

        addItemButton.setOnClickListener {
            val title = titleEditTextView.text.toString()
            val desc = descriptionEditTextView.text.toString()
            caller.addItems(title,desc)
            titleEditTextView.text.clear()
            descriptionEditTextView.text.clear()
        }

    }







    internal inner class AddItemAdapter(var items: MutableList<Item>) :
        RecyclerView.Adapter<AddItemAdapter.AddItemViewHolder>() {


        inner class AddItemViewHolder(inflater: View) : RecyclerView.ViewHolder(inflater)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
            return AddItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_add_item, parent, false)
            )


        }

        override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
            val title: TextView = holder.itemView.findViewById(R.id.itemTitleTextView)
            val desc: TextView = holder.itemView.findViewById(R.id.itemDescTextView)
            val container: LinearLayout = holder.itemView.findViewById(R.id.itemContainer)

            container.setBackgroundColor(items[position].color)
            title.text = items[position].title



            desc.text = items[position].desc


          //  desc.setBackgroundColor(items[position].color)

        }

        override fun getItemCount(): Int {
            return items.size
        }
    }





}

private fun Spinner.onItemSelectedListener(onItemSelectedListener: AdapterView.OnItemSelectedListener) {

}


