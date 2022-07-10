package com.devour.reviewerapp.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
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


    fun refresh(){
        CoroutineScope(Dispatchers.IO).launch {

            subjectWithTerms = AppData.db.reviewerDao().getSubjectsById(subjectId)
        }
    }

     var subjectId:Int = -1
     lateinit var subject:Subject


    var termId = -1
    var topicId=-1


    var termPosition =-1
    var topicPosition = -1



    lateinit var addFragment:AddFragment
    lateinit var viewFragment:ViewFragment
    lateinit var searchFragment : SearchFragment



    private var  subjectWithTerms:SubjectWithTerms?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        addFragment= AddFragment.newInstance()

        viewFragment = ViewFragment()
        searchFragment = SearchFragment()



        val bundle= intent.getBundleExtra("bundle")

        subjectWithTerms = bundle?.getSerializable("subjectWithTerms") as SubjectWithTerms
        subject = subjectWithTerms!!.subject
        subjectId = subject.subjectId

        if(subjectWithTerms!!.termsWithTopics.isEmpty()){
            termPosition=-1
        }


        AppData.db = ReviewerDatabase.getDataBase(this)

//
//        CoroutineScope(Dispatchers.IO).launch {
//            subjectWithTerms =AppData.db.reviewerDao().getSubjectsById(subjectId)
//
//
//
//
//
//        }
        firstTimeOpening()



        setFragment()

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


            }

            refresh()



            termPosition = subjectWithTerms!!.termsWithTopics.size
            Log.i("TermPos", "$termPosition")
            updateTermId(termPosition-1)


            reloadFragment(addFragment)

        }


        builder.setNegativeButton("Cancel") {dialogue, which->


        }

        val dialouge : AlertDialog =builder.create()
        dialouge.show()



    }

    fun updateTermId(pos:Int){
        Log.i("TermPos", "$pos + ${subjectWithTerms!!.termsWithTopics.size}")
        if( subjectWithTerms!!.termsWithTopics.isNotEmpty()){
            termId = subjectWithTerms!!.termsWithTopics[pos].term.termId
        }

    }

    fun addTopic(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New Topic")
        builder.setMessage("Enter Topic Name")

        val myInput = EditText(this)
        builder.setView(myInput)

        builder.setPositiveButton("Save") {dialogue, which->


            val newTopic = Topic(myInput.text.toString(),subjectId = subjectId, termId = subjectWithTerms!!.termsWithTopics[termPosition].term.termId, timeStamp = System.currentTimeMillis(), color = subject.color)

            CoroutineScope(Dispatchers.IO).launch {

                insertTopic(newTopic)





                withContext(Dispatchers.Main){
                    reloadFragment(addFragment)
                }
            }
            refresh()

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









    private fun insertTerms(term: Term){
        AppData.db.reviewerDao().insertTerms(term)
    }
    private fun insertTopic(topic: Topic){
        AppData.db.reviewerDao().insertTopic(topic)
    }
    private fun insertItem(item: Item){
        AppData.db.reviewerDao().insertItem(item)
    }

    override fun addItems(title: String, desc: String): MutableList<Item> {
        return mutableListOf()
    }

    override fun loadTermSpinner(): MutableList<TermWithTopics> {

//        var items = mutableListOf<String>()
//        for(terms in subjectWithTerms!!.termsWithTopics ){
//            items.add(terms.term.name)
//        }

        return  subjectWithTerms!!.termsWithTopics

    }


    override fun getTermPos(): Int {

        return termPosition
    }
    override fun loadTopicSpinner(): MutableList<TopicWithItems> {
//        var items = mutableListOf<String>()
//
//        for(topic in subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems){
//            items.add(topic.topic.title)
//        }

        return if(termPosition>=0){
            subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems
        }else{
            mutableListOf()
        }



    }



    override fun onAddTermClick() {
        addTerms()


    }

    override fun onAddTopicClick() {
        addTopic()
    }

    override fun onTermSelectedItem(position: Int) {
        termPosition = position


    }

    override fun onTopicSelectedItem(position: Int) {
        topicPosition = position
    }


}