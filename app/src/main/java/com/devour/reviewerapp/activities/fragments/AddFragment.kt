package com.devour.reviewerapp.activities.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Delete
import com.devour.reviewerapp.R
import com.devour.reviewerapp.activities.components.SyntaxHighlighter
import com.devour.reviewerapp.data.relationship.TermWithTopics
import com.devour.reviewerapp.data.relationship.TopicWithItems
import com.devour.reviewerapp.model.Item
import com.devour.reviewerapp.model.Subject
import com.google.android.material.button.MaterialButton


interface AddFragmentListener {

    fun getSubjectInstance():Subject
    fun addItems(title: String, desc: String)
    fun loadTermSpinner(): MutableList<TermWithTopics>
    fun loadTopicSpinner(): MutableList<TopicWithItems>
    fun onAddTermClick()
    fun onAddTopicClick()
    fun getTermPos(): Int
    fun getTopicPos():Int

    fun retrieveAllItems():MutableList<Item>

    fun onTermSelectedItem(position: Int)
    fun onTopicSelectedItem(position: Int)

    fun copyToClipBoard(text:String)

    fun onDeleteItem(itemID:Int, position: Int)

}

class AddFragment() : Fragment() {

    lateinit var caller: AddFragmentListener

    private var spinnerInitialization = false
    private var _spinnerInitialization = false
    var topicWithItems:MutableList<TopicWithItems> ?= null
    private var termWithTopics :MutableList<TermWithTopics>?=null

    lateinit var addItemAdapter: AddItemAdapter


    lateinit var _context: Context

    var onAdd = false





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


         _context = activity as Context


        loadUI(fragment)
        loadTermSpinner(_context, fragment)

         loadTopicSpinner( _context, fragment)



        val items = caller.retrieveAllItems()


        val linearLayout = LinearLayoutManager(context)
        linearLayout.stackFromEnd =true
        linearLayout.reverseLayout = false

        val layoutManager: RecyclerView.LayoutManager = linearLayout

        addItemRecyclerView.layoutManager = layoutManager
        addItemAdapter =AddItemAdapter(items)
        addItemRecyclerView.adapter = addItemAdapter
        loadComponents(_context, fragment,addItemRecyclerView)
        addItemRecyclerView.scrollToPosition(items.size-1)

        return fragment
    }

    fun loadUI(fragment: View){

        val lowerContainer:ConstraintLayout = fragment.findViewById(R.id.LowerConstraintLayout)
        val subject = caller.getSubjectInstance()
        lowerContainer.setBackgroundColor(subject.color)

    }



    private fun loadComponents(context: Context, fragment: View, recyclerView: RecyclerView, ) {
        val addTermButton: com.google.android.material.button.MaterialButton = fragment.findViewById(R.id.addTermButton)
        val addTopicButton: com.google.android.material.button.MaterialButton = fragment.findViewById(R.id.addTopicButton)

        val addItemButton:com.google.android.material.button.MaterialButton = fragment.findViewById(R.id.addItemButton)
        val titleEditTextView :EditText = fragment.findViewById(R.id.titleEditTextView)
        val descriptionEditTextView :EditText = fragment.findViewById(R.id.descriptionEditTextView)

        addItemButton.setOnClickListener {
            val title = titleEditTextView.text.toString()
            val desc = descriptionEditTextView.text.toString()
             caller.addItems(title,desc)

            titleEditTextView.text.clear()
            descriptionEditTextView.text.clear()

            val items= caller.retrieveAllItems()

            onAdd =true

            addItemAdapter =AddItemAdapter(items)
            recyclerView.adapter = addItemAdapter
            addItemAdapter.notifyItemInserted(items.size)




        }


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




    }



    companion object {
        fun newInstance(): AddFragment {
            return AddFragment()
        }
    }



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

        if(position>=0) {
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
            val position =caller.getTopicPos()
            topicSpinner.setSelection(position)
            Log.i("positionlog", "position ${position}")


            val aa = ArrayAdapter(
                context,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                topics
            )
            aa.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
            aa.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)


            topicSpinner.adapter = aa
            if (position >= 0) {

                topicSpinner.post(Runnable {
                    kotlin.run {
                        topicSpinner.setSelection(position)
                    }
                })

            }
        }


    }









    inner class AddItemAdapter(var items: MutableList<Item> ) :
        RecyclerView.Adapter<AddItemAdapter.AddItemViewHolder>() {


        private fun popupMenu(context: Context, view: View, item: Item, position: Int) {
            // creating a object of Popupmenu

            val popupMenu = PopupMenu(context, view)

            // we need to inflate the object
            // with popup_menu.xml file
            popupMenu.inflate(R.menu.popup_menu)

            // adding click listener to image
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.copy -> {

                    caller.copyToClipBoard(item.title+"\n"+item.desc)
                    Toast.makeText(_context, "Copied to ClipBoard",Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.delete -> {




                        deleteItem(item.itemId,position)



                        Toast.makeText(_context, "Item Deleted",Toast.LENGTH_SHORT).show()

                        true
                    }

                    else -> {
                        true
                    }
                }

            }

            // event on long press on image
            view.setOnClickListener {
                try {
                    val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                    popup.isAccessible = true
                    val menu = popup.get(popupMenu)
                    menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(menu,true)
                }
                catch (e: Exception)
                {
                    Log.d("error", e.toString())
                }
                finally {
                    popupMenu.show()
                }


            }
        }


        fun deleteItem(itemID: Int, position: Int){




            caller.onDeleteItem(itemID, position )

            notifyItemRemoved(position)
        }





        inner class AddItemViewHolder(inflater: View) : RecyclerView.ViewHolder(inflater){
            private var title: TextView? =null
            private var desc: TextView ? =null
            private var container: LinearLayout ? =null

            init {
                title = itemView.findViewById(R.id.itemTitleTextView)
                desc = itemView.findViewById(R.id.itemDescTextView)
                container = itemView.findViewById(R.id.itemContainer)
            }

            fun bind(item: Item, position: Int){

                val highlight = SyntaxHighlighter.highlight()
            //  container!!.setBackgroundColor(R.color.black)
                title!!.text = item.title

                val _desc = item.desc





                if(  _desc.endsWith("```")){


                    desc!!.text =  _desc.dropLast(3)

                    highlight.setSpan(desc)
                }else{
                    desc!!.text = _desc
                }


                val copyView:View = itemView.findViewById(R.id.copyView)

                popupMenu(_context, copyView, item, position)


            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
            return AddItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_add_item, parent, false)
            )


        }

        override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {

            holder.bind(item = items[position],position)



            if (onAdd){

                if(position==items.size-1) {
                    val anim =
                        AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_up)
                    holder.itemView.startAnimation(anim)
                }

            }
            onAdd=false
//                if(position==deletePos-1) {
//                    val anim =
//                        AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_in)
//                    holder.itemView.startAnimation(anim)
//                }
//            }


          //  desc.setBackgroundColor(items[position].color)

        }

        override fun getItemCount(): Int {
            return items.size
        }
    }



}


private fun Spinner.onItemSelectedListener(onItemSelectedListener: AdapterView.OnItemSelectedListener) {

}


