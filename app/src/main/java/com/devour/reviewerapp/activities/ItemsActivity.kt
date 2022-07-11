package com.devour.reviewerapp.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.devour.reviewerapp.R
import com.devour.reviewerapp.activities.fragments.AddFragment
import com.devour.reviewerapp.activities.fragments.AddFragmentListener
import com.devour.reviewerapp.activities.fragments.SearchFragment
import com.devour.reviewerapp.activities.fragments.ViewFragment
import com.devour.reviewerapp.data.data_source.AppData
import com.devour.reviewerapp.data.data_source.ReviewerDatabase
import com.devour.reviewerapp.data.relationship.SubjectWithTerms
import com.devour.reviewerapp.data.relationship.TermWithTopics
import com.devour.reviewerapp.data.relationship.TopicWithItems
import com.devour.reviewerapp.model.Item
import com.devour.reviewerapp.model.Subject
import com.devour.reviewerapp.model.Term
import com.devour.reviewerapp.model.Topic
import kotlinx.coroutines.*

class ItemsActivity : AppCompatActivity(), AddFragmentListener {


    private fun refresh(){
     CoroutineScope(Dispatchers.IO).launch {

            subjectWithTerms = AppData.db.reviewerDao().getSubjectsById(subjectId)
        }




    }

     var subjectId:Int = -1
     lateinit var subject:Subject


    var termId = -1
    var topicId=-1


    var termPosition =0
    var topicPosition = -1



    lateinit var addFragment:AddFragment
    lateinit var viewFragment:ViewFragment
    lateinit var searchFragment : SearchFragment

    var items = mutableListOf<Item>()



    private var  subjectWithTerms:SubjectWithTerms?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        addFragment= AddFragment.newInstance()

        viewFragment = ViewFragment()
        searchFragment = SearchFragment()



        val bundle= intent.getBundleExtra("bundle")

        subjectWithTerms = bundle?.getSerializable("subjectWithTerms") as SubjectWithTerms
        subject = subjectWithTerms!!.subject
        subjectId = subject.subjectId

        if(subjectWithTerms!!.termsWithTopics.isEmpty()){
            termPosition=-1
        }else{
            termId= subjectWithTerms!!.termsWithTopics[termPosition].term.termId
        }



        AppData.db = ReviewerDatabase.getDataBase(this)

        refresh()


        setFragment()

        getAllItems()

    }

    private  fun setCurrentFragment(fragment: Fragment){

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment, )
                .commit()
        }
    }

    private  fun reloadFragment(fragment: Fragment){



            supportFragmentManager.beginTransaction().detach(fragment).commit()
        supportFragmentManager.beginTransaction().attach(fragment).commit()
  //     supportFragmentManager.beginTransaction().replace(R.id.flFragment, fragment,"fragtag").commit()



    }

    private fun setFragment(){

        setCurrentFragment(addFragment)

        val bottomNavigation :com.google.android.material.bottomnavigation.BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.add ->  setCurrentFragment(addFragment)
                R.id.view ->  setCurrentFragment(viewFragment)
                R.id.search ->  setCurrentFragment(searchFragment)
            }
            true

        }
    }



    fun addTerms(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New Term")
        builder.setMessage("Enter Term Name")

        val myInput = EditText(this)
        builder.setView(myInput)

        builder.setPositiveButton("Save") {dialogue, which->
            val newTerms = Term(myInput.text.toString(), subjectId,System.currentTimeMillis(),subject.color)

            CoroutineScope(Dispatchers.IO).launch {

            insertTerms(newTerms)

                subjectWithTerms = AppData.db.reviewerDao().getSubjectsById(subjectId)

            }
            termPosition = subjectWithTerms!!.termsWithTopics.size
            updateId(termPosition-1)








            reloadFragment(addFragment)




        }


        builder.setNegativeButton("Cancel") {dialogue, which->


        }

        val dialouge : AlertDialog =builder.create()
        dialouge.show()



    }

    fun updateId(pos:Int){

        if( subjectWithTerms!!.termsWithTopics.isNotEmpty()){
            termId = subjectWithTerms!!.termsWithTopics[pos].term.termId
            Log.i("TermId", "$termId")

        }

    }

    fun updateTopicId(pos:Int){

        if(termPosition>=0){
            if( subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems.isNotEmpty()){
                topicId=subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems[pos].topic.topicId
                Log.i("TermId", "$topicId")
            }


        }

    }



    fun addTopic(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New Topic")
        builder.setMessage("Enter Topic Name")

        val myInput = EditText(this)
        builder.setView(myInput)

        builder.setPositiveButton("Save") {dialogue, which->

            if(subjectWithTerms!!.termsWithTopics.isEmpty()){
                Toast.makeText(this, "Terms is empty",Toast.LENGTH_SHORT).show()
            }else {


                val newTopic = Topic(
                    myInput.text.toString(),
                    subjectId = subjectId,
                    termId = subjectWithTerms!!.termsWithTopics[termPosition].term.termId,
                    timeStamp = System.currentTimeMillis(),
                    color = subject.color
                )

                CoroutineScope(Dispatchers.IO).launch {

                    insertTopic(newTopic)


                }
                refresh()
                topicPosition = subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems.size


                updateTopicId(topicPosition - 1)


                Log.i("TermPos", "termID: ${termId} topicID:$topicId")
                reloadFragment(addFragment)
            }


        }

        builder.setNegativeButton("Cancel") {dialogue, which->


        }

        val dialouge : AlertDialog =builder.create()
        dialouge.show()



    }



     fun firstTimeOpening(){

       //  Log.i("myLogger", "${termWithTopics!!.size} ${termWithTopics!![0].term.name}")

        if(subjectWithTerms!!.termsWithTopics.isEmpty()){

            addTerms()
        }else{

            CoroutineScope(Dispatchers.IO).launch {

                subjectWithTerms  = AppData.db.reviewerDao().getSubjectsById(subjectId)

            }
        }

    }


    fun addNewItem(item: Item){




        CoroutineScope(Dispatchers.IO).launch {
            insertItem(item)
        }

    }


     fun getAllItems(){


        CoroutineScope(Dispatchers.IO).launch{
            items = AppData.db.reviewerDao().getAllItems(subjectId)
            Log.i("ItemsLog", items.toString()+"subjectId ${subjectId}")
        }



    }








    private fun insertTerms(term: Term){
        AppData.db.reviewerDao().insertTerms(term)
    }
    private fun insertTopic(topic: Topic){
        AppData.db.reviewerDao().insertTopic(topic)
    }
    private fun insertItem(item: Item){
        AppData.db.reviewerDao().insertItem(item)
    }


    override fun loadTermSpinner(): MutableList<TermWithTopics> {

        return  subjectWithTerms!!.termsWithTopics

    }


    override fun getTermPos(): Int {

        return termPosition
    }


    override fun loadTopicSpinner(): MutableList<TopicWithItems> {

        return if(termPosition>=0){
            subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems
        }else{
            mutableListOf()
        }



    }


    override fun retrieveAllItems(): MutableList<Item> {
        return items
    }


    override fun onAddTermClick() {
        addTerms()


    }

    override fun onAddTopicClick() {

            addTopic()


    }

    override fun onTermSelectedItem(position: Int) {
        termPosition = position
        termId =subjectWithTerms!!.termsWithTopics[termPosition].term.termId


    }

    override fun onTopicSelectedItem(position: Int) {
        topicPosition = position
        topicId= subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems[topicPosition].topic.topicId

    }

    override fun addItems(title: String, desc: String): MutableList<Item> {
        var _termId=-1
        if(termPosition >=0){

            _termId= subjectWithTerms!!.termsWithTopics[termPosition].term.termId

        }


        val newItem = Item(title,desc,topicId,_termId,subjectId,System.currentTimeMillis(),subject.color )
        addNewItem(newItem)

        getAllItems()

        items.clear()

        reloadFragment(addFragment)

        Log.i("ItemsLog", items.toString()+"subjectId ${subjectId}")
        items.sortBy { it.timeStamp }

        return items
    }



}