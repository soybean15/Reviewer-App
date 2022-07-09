package com.devour.reviewerapp.activities.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.activities.components.SubjectViewHolder
import com.devour.reviewerapp.data.relationship.TermWithTopics
import com.devour.reviewerapp.model.Item
import com.devour.reviewerapp.model.Subject



interface AddFragmentListener{
    fun addItems(title:String,desc:String):MutableList<Item>
    fun loadTermSpinner():MutableList<String>
    fun loadTopicSpinner():MutableList<String>
    fun onAddTermClick()
    fun getTermPos():Int

}

class AddFragment() : Fragment() {

    lateinit var caller:AddFragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is AddFragmentListener){
            caller = context
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragment =inflater.inflate(R.layout.fragment_add, container, false)


        val addItemRecyclerView:RecyclerView = fragment.findViewById(R.id.AddItemRecyclerView)




        val context =activity as Context




        loadSpinner(context, fragment)
        loadComponents(context,fragment)

        val layoutManager:RecyclerView.LayoutManager = LinearLayoutManager(context )
        addItemRecyclerView.layoutManager =layoutManager
       // addItemRecyclerView.adapter =AddItemAdapter(items)





        return fragment
    }

    companion object{
        fun newInstance():AddFragment{
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
  fun loadSpinner(context: Context, fragment:View){
     val termsSpinner: Spinner = fragment.findViewById(R.id.termSpinner)
     val topicSpinner: Spinner = fragment.findViewById(R.id.topicSpinner)

     val terms = caller.loadTermSpinner()
        val position = caller.getTermPos()
        Log.i("positiontag", "pos $position")
    // val  topics= caller.loadTopicSpinner()

            termsSpinner.setSelection(position)


     val aa = ArrayAdapter(context, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,terms)
     aa.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
    // val aa2 = ArrayAdapter(context, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,topics)
     aa.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
    // aa2.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)



     termsSpinner.adapter =aa
    if(position>=0) {
        termsSpinner.post(Runnable {
            kotlin.run {
                termsSpinner.setSelection(position)
            }
        })
    }
   //  topicSpinner.adapter=aa2
 }

    fun loadComponents(context: Context,fragment: View){
        val addTermButton:Button = fragment.findViewById(R.id.addTermButton)

        addTermButton.setOnClickListener {
            caller.onAddTermClick()


        }
    }


}


private class AddItemAdapter(var  items: MutableList<Item>):
    RecyclerView.Adapter<AddItemAdapter.AddItemViewHolder>() {





    inner class AddItemViewHolder(inflater: View) : RecyclerView.ViewHolder(inflater)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        return AddItemViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_add_item, parent,false))


    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        val title:TextView = holder.itemView.findViewById(R.id.itemTitleTextView)
        val desc: TextView = holder.itemView.findViewById(R.id.itemDescTextView)

        title.text = items[position].title
        desc.text = items[position].desc
    }

    override fun getItemCount(): Int {
        return items.size
    }
}