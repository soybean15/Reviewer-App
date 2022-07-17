package com.devour.reviewerapp.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.devour.reviewerapp.R
import com.devour.reviewerapp.activities.fragments.*
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


class ItemsActivity : AppCompatActivity(), AddFragmentListener,ViewFragmentListener{

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


    var itemsWithoutTerms:MutableList<Item> = mutableListOf()


    private var  subjectWithTerms:SubjectWithTerms?=null

    lateinit var sharedPreferences:SharedPreferences



    fun loadUI(){
        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        val subjectText :TextView = findViewById(R.id.subjectTitleTextView)

        subjectText.text = subject.name

        toolbar.setBackgroundColor(subject.color)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



    private fun refresh(){
     CoroutineScope(Dispatchers.IO).launch {

            subjectWithTerms = AppData.db.reviewerDao().getSubjectsById(subjectId)
        }




    }



    private fun sharedPreference(){
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)


        termPosition=sharedPreferences.getInt(subject.name+subjectId,-1)

        if(termPosition >= 0){
            termId= subjectWithTerms!!.termsWithTopics[termPosition].term.termId

            Log.i("Items3Log", "oncreate"+subject.name+subjectId+termId+" termposition ${termPosition}")
            topicPosition= sharedPreferences.getInt( subject.name+subjectId+termId ,-1)

            if(topicPosition>=0){

                try{
                    topicId = subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems[topicPosition].topic.topicId
                    Log.i("Items3Log", "oncreate"+subject.name+subjectId+termId+" topicposition ${topicPosition}")

                }catch (e:Exception){
                    topicPosition = -1
                    topicId=-1
                }

            }

        }

    }

    private fun addSharedPref(key:String, value:Int){
        with(sharedPreferences.edit()){

            putInt(key,value).commit()
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)



        AppData.db = ReviewerDatabase.getDataBase(this)



        val bundle= intent.getBundleExtra("bundle")

        subjectWithTerms = bundle?.getSerializable("subjectWithTerms") as SubjectWithTerms
        subject = subjectWithTerms!!.subject
        subjectId = subject.subjectId

        loadUI()

        refresh()

        viewFragment = ViewFragment.newInstance(subjectWithTerms!!.termsWithTopics)
        addFragment= AddFragment.newInstance()
        searchFragment = SearchFragment()


        setFragment()

        getAllItems()



        sharedPreference()
        loadItemsWithoutTerms()

    }

    override fun copyToClipBoard(text:String){
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

    private  fun setCurrentFragment(fragment: Fragment){

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
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

                awaitAll(
                    async { insertTerms(newTerms) },
                    async {
                        subjectWithTerms = AppData.db.reviewerDao().getSubjectsById(subjectId)
                    })
                subjectWithTerms = AppData.db.reviewerDao().getSubjectsById(subjectId)

                withContext(Dispatchers.IO){


                }





            }
            termPosition = subjectWithTerms!!.termsWithTopics.size
            addSharedPref(subject.name+subjectId, termPosition)

           // updateId(termPosition-1)
            Log.i("termlog", "Add term${subject.name+subjectId}")

            updateSubjectTimeStamp()
            reloadFragment(addFragment)





        }


        builder.setNegativeButton("Cancel") {dialogue, which->


        }

        val dialouge : AlertDialog =builder.create()
        dialouge.show()



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


                    awaitAll(
                        async { insertTopic(newTopic) },
                        async {
                            subjectWithTerms = AppData.db.reviewerDao().getSubjectsById(subjectId)
                        })


                    subjectWithTerms = AppData.db.reviewerDao().getSubjectsById(subjectId)


                }

                topicPosition = subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems.size
                //updateTopicId(topicPosition -1)
                addSharedPref(subject.name+subjectId+termId, topicPosition)


                Log.i("positionlog", "from Main: ${topicPosition} ")
                updateSubjectTimeStamp()
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

    private fun updateSubjectTimeStamp(){
        CoroutineScope(Dispatchers.IO).launch {
            AppData.db.reviewerDao().updateSubjectTimeStamp(subjectId,System.currentTimeMillis())
        }

    }


    override fun loadTermSpinner(): MutableList<TermWithTopics> {

        return  subjectWithTerms!!.termsWithTopics

    }


    override fun getTermPos(): Int {

        return termPosition
    }

    override fun getSubjectInstance(): Subject {
        return subject
    }

    override fun getTopicPos(): Int {

        //when changing terms, check if selected term items is as many as the previous selected term items
        if(subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems.size <=topicPosition){

            return -1
        }
        return topicPosition
    }


    override fun loadTopicSpinner(): MutableList<TopicWithItems> {

        return if(termPosition>=0){
            subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems
        }else{
            mutableListOf()
        }



    }




    override fun retrieveAllItems(): MutableList<Item> {

        getAllItems()

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


        addSharedPref(subject.name+subjectId, termPosition)

    }

    override fun onTopicSelectedItem(position: Int) {
        topicPosition= sharedPreferences.getInt( subject.name+subjectId+termId ,-1)

            topicPosition = position
            addSharedPref(subject.name + subjectId + termId, topicPosition)



    }

    override fun addItems(title: String, desc: String) {

        if( subjectWithTerms!!.termsWithTopics.size>0){

            termId= subjectWithTerms!!.termsWithTopics[termPosition].term.termId
            if(subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems.isNotEmpty() && subjectWithTerms!!.termsWithTopics.isNotEmpty()){
                topicId= subjectWithTerms!!.termsWithTopics[termPosition].topicWithItems[topicPosition].topic.topicId
            }

        }


        val newItem = Item(title,desc,topicId,termId,subjectId,System.currentTimeMillis(),subject.color )
        addNewItem(newItem)

        updateSubjectTimeStamp()
        items.clear()
        items.sortBy { it.timeStamp }


        reloadFragment(addFragment)

    }

    override fun onDeleteItem(itemID: Int, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
           awaitAll(
               async {AppData.db.reviewerDao().deleteItem(itemID)  },
               async {getAllItems()  }
           )

        }
        updateSubjectTimeStamp()



        reloadFragment(addFragment)

   }

    fun loadItemsWithoutTerms(){

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Default) {
                itemsWithoutTerms = AppData.db.reviewerDao().getItemsWithoutTerms(subjectId)
                Log.i("SIzeLog", "Size from inside coroutine ${items.size}")
            }


        }
        Log.i("SIzeLog", "Size from outside coroutine ${items.size}")
    }



    override fun getItemWithoutTerm() :MutableList<Item>{
        loadItemsWithoutTerms()
        return itemsWithoutTerms
    }


}