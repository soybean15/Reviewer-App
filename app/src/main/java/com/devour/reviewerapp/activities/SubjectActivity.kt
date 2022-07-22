package com.devour.reviewerapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager

import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginStart
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.activities.components.OnSubjectClickListener
import com.devour.reviewerapp.activities.components.SubjectAdapter
import com.devour.reviewerapp.activities.components.SwipeHelper
import com.devour.reviewerapp.data.data_source.AppData
import com.devour.reviewerapp.data.data_source.ReviewerDatabase
import com.devour.reviewerapp.data.relationship.SubjectWithTerms
import com.devour.reviewerapp.model.Subject
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.io.File
import java.io.Serializable

import kotlin.Exception


class SubjectActivity : AppCompatActivity() ,OnSubjectClickListener{

    private var subjectAdapter:SubjectAdapter? = null
    private var subjectRecyclerView:RecyclerView?=null
    private var _position = -1


    private var subjectWithTerms = mutableListOf<SubjectWithTerms>()


    override fun onAnimateSubjectClick(view: View,index: Int) {

        if(index==_position){
            val anim = AnimationUtils.loadAnimation(view.context, R.anim.slide_in)
            view.startAnimation(anim)

        }

    }

    override fun onResume() {
        super.onResume()
       loadSubjects()
    }



    override fun onSubjectClick(subjectWithTerms: SubjectWithTerms) {

        val bundle = Bundle()
        bundle.putSerializable("subjectWithTerms", subjectWithTerms )
        val intent = Intent(this, ItemsActivity::class.java)
        intent.putExtra("bundle", bundle)
        startActivity(intent)



        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
    }






    var offSearch = false
    var _hasFocus = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)


        subjectRecyclerView = findViewById(R.id.subjectRecyclerView)



        val layoutManager:RecyclerView.LayoutManager = LinearLayoutManager(this, )

        subjectRecyclerView!!.layoutManager =layoutManager





        subjectWithTerms=AppData.subject
        subjectAdapter = SubjectAdapter(subjectWithTerms,this )
        subjectRecyclerView!!.adapter = subjectAdapter

        setUpRecyclerView()

        subjectRecyclerView!!.scheduleLayoutAnimation()


        AppData.db = ReviewerDatabase.getDataBase(this)



        dbConnect()

        searchItem()






    }


    private fun setUpRecyclerView() {
        subjectRecyclerView!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        subjectRecyclerView!!.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper( subjectRecyclerView!!) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {

                val deleteButton = deleteButton(position)
                val editButton = editButton(position)
                //                val markAsUnreadButton = markAsUnreadButton(position)
//                val archiveButton = archiveButton(position)
//                when (position) {
//                    1 -> buttons = listOf(deleteButton)
//                    2 -> buttons = listOf(deleteButton, markAsUnreadButton)
//                    3 -> buttons = listOf(deleteButton, markAsUnreadButton, archiveButton)
//                    else -> Unit
//                }
                return listOf(deleteButton, editButton)
            }
        })

        itemTouchHelper.attachToRecyclerView(subjectRecyclerView!!)
    }




    private fun databaseFileExist():Boolean{
        return try {
            File(getDatabasePath(AppData.dbFileName).absolutePath).exists()
        }catch (e: Exception){
            false
        }
    }


    fun createSubject(v: View){




        val builder = AlertDialog.Builder(this)
        val linearLayout= LinearLayout(this)

        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(40,0,40,0)



        builder.setTitle("New Subject")
        builder.setMessage("Enter Subject Name")




        val myInput = EditText(this)




        myInput.layoutParams = params
        myInput.height = getPixelDensity(40f)
        myInput.setPadding(40,0,20,0)

        val shape = GradientDrawable()
        shape.cornerRadius = 40f



        shape.setStroke(2,resources.getColor(R.color.gray) )


        myInput.background = shape


        linearLayout.addView(myInput)




        builder.setView(linearLayout)

        builder.setPositiveButton("Save") {dialogue, which->

            val subjectName:String = myInput.text.toString()

            if (subjectName.isBlank()) {
               // snackBar(v,"Subject Name is Empty")
                snackBar(findViewById(android.R.id.content),"Subject Name is Empty")
            }else{




                val newSubject = Subject(subjectName,System.currentTimeMillis(), resources.getIntArray(R.array.random_colors).random())
                val newSubjectWithTerms = SubjectWithTerms(newSubject, mutableListOf())

                subjectWithTerms.add(newSubjectWithTerms)

                CoroutineScope(Dispatchers.IO).launch {
                    try{

                       val task=  listOf(async {
                            AppData.db.reviewerDao().insertSubject(newSubject)
                        },async {
                            subjectWithTerms  = AppData.db.reviewerDao().getSubjectWithTerms()
                        })
                        task.joinAll()

                        subjectWithTerms.sortByDescending { it.subject.timeStamp }



                        withContext(Dispatchers.Main){




                            subjectAdapter = SubjectAdapter(subjectWithTerms,this@SubjectActivity)
                            subjectRecyclerView!!.adapter = subjectAdapter

                            // subjectAdapter!!.notifyDataSetChanged()


                            _position=0

                            subjectAdapter!!.notifyItemInserted(0)



                            //  subjectRecyclerView.scrollToPosition(0)
                        }


                    }catch (e:Exception){

                    }


             //


                }

            }







        }
        builder.setNegativeButton("Cancel") {dialogue, which->

        }

        val dialouge : AlertDialog =builder.create()
        dialouge.show()
        dialouge.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.blue_1))
        dialouge.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.blue_1))
    }


    fun getPixelDensity(num:Float):Int{
        val dpRatio = this.resources.displayMetrics.density
        return (num*dpRatio).toInt()
    }





    fun editSubject(position: Int,name: String, timeStamp:Long, subjectId:Int){


        val builder = AlertDialog.Builder(this)

        val linearLayout= LinearLayout(this)


        builder.setTitle("Edit Subject")
        builder.setMessage("Enter New Subject Name")


        val myInput = EditText(this)

        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(40,0,40,0)



        myInput.layoutParams = params
        myInput.height = getPixelDensity(40f)
        myInput.setPadding(40,0,20,0)
//        myInput.inputType = InputType.TYPE_CLASS_TEXT


        val shape = GradientDrawable()
        shape.cornerRadius = 40f



        shape.setStroke(2,resources.getColor(R.color.gray) )


        myInput.background = shape



        myInput.setText(name)
        myInput.setSelectAllOnFocus(true)

        linearLayout.addView(myInput)

        builder.setView(linearLayout)

        builder.setPositiveButton("Save") {dialogue, which->

            CoroutineScope(Dispatchers.IO).launch {

                try {
//
//                   val task = listOf(async {
//                       AppData.db.reviewerDao().updateSubject(
//                           myInput.text.toString(),
//                           timeStamp,
//                           subjectId
//                       )
//
//                   },async {
//                        AppData.db.reviewerDao().getSubjectWithTerms()
//                   })
//
//                    task.joinAll()

                    AppData.db.reviewerDao().updateSubject(
                           myInput.text.toString(),
                           timeStamp,
                           subjectId
                       )



                       subjectWithTerms[position].subject.name = myInput.text.toString()
                       subjectWithTerms[position].subject.timeStamp =timeStamp
                       subjectWithTerms.sortByDescending { it.subject.timeStamp }



                       withContext(Dispatchers.Main){



                           Log.i("Edit", "position:$position, nam:$name")
                           subjectAdapter = SubjectAdapter(subjectWithTerms,this@SubjectActivity)
                           subjectRecyclerView!!.adapter = subjectAdapter
                           subjectAdapter!!.notifyItemChanged(0)
                           //subjectAdapter!!.notifyDataSetChanged()

                           //subjectRecyclerView.scheduleLayoutAnimation()



                           //  subjectRecyclerView.scrollToPosition(0)
                       }


                }catch (e:Exception){
                    snackBar(findViewById(android.R.id.content),e.toString())
                }




            }





        }
        builder.setNegativeButton("Cancel") {dialogue, which->

        }

        val dialouge : AlertDialog =builder.create()
        dialouge.show()
        dialouge.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.blue_1))
        dialouge.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.blue_1))
    }

    private fun dbConnect(){

        if(databaseFileExist()){//read content
            loadSubjects()

        }else{//first Time opening the app
            //AppData.initialize(this)
            subjectAdapter = SubjectAdapter(subjectWithTerms,this)
            subjectRecyclerView!!.adapter = subjectAdapter

            CoroutineScope(Dispatchers.IO).launch {
                for (groupWithItems in subjectWithTerms) {
                    AppData.db.reviewerDao().insertSubject(groupWithItems.subject)


                }
            }
        }
    }


    private fun loadSubjects(){
        CoroutineScope(Dispatchers.IO).launch {
            subjectWithTerms =AppData.db.reviewerDao().getSubjectWithTerms()
            subjectWithTerms.sortByDescending { it.subject.timeStamp }

            withContext(Dispatchers.Main){
                subjectAdapter = SubjectAdapter(subjectWithTerms,this@SubjectActivity)
                subjectRecyclerView!!.adapter = subjectAdapter
                subjectRecyclerView!!.scheduleLayoutAnimation()
            }
            subjectRecyclerView!!.scheduleLayoutAnimation()
        }
    }

    fun searchItem(){
        val searchTextView:EditText = findViewById(R.id.searchTextView)

        keyPressEvent(searchTextView)

        searchTextView.setOnFocusChangeListener{_,hasFocus->

          //  var param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 40)
            val param:ViewGroup.MarginLayoutParams = searchTextView.layoutParams as ViewGroup.MarginLayoutParams
            val dpRatio = this.resources.displayMetrics.density
            val margin = (5*dpRatio).toInt()


                if (hasFocus){

                    _hasFocus = hasFocus
                    val anim = TranslateAnimation(searchTextView.width.toFloat(), 0f,0f,0f)
                    anim.duration =500
                    anim.fillAfter =true
                    param.setMargins(0,margin,0,margin)
                    searchTextView.requestLayout()
                    searchTextView.startAnimation(anim)



                }else{
                    _hasFocus = !hasFocus
                    val anim = TranslateAnimation(searchTextView.width.toFloat(), 0f,0f,0f)
                    anim.duration =500
                    val start = (70*dpRatio).toInt()
                    param.setMargins(start,margin,0,margin)


                    searchTextView.requestLayout()
                    searchTextView.startAnimation(anim)

                }



        }

        searchTextView.setOnKeyListener { view, keyCode, event ->

            if(keyCode == KeyEvent.KEYCODE_ENTER){
                if(event.action == KeyEvent.ACTION_DOWN){



                    searchSubject(searchTextView)



                    val inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(view.windowToken,0)

                }
            }
            false
        }




    }

    fun searchSubject( text: EditText){
        val name :String = text.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
             subjectWithTerms = AppData.db.reviewerDao().getSubjects(name)

            withContext(Dispatchers.Main){
                if(subjectWithTerms.size==0){
                    snackBar(  subjectRecyclerView!!, "No Item Found")
                }
                    subjectWithTerms.sortByDescending { it.subject.timeStamp }
                    subjectAdapter = SubjectAdapter(subjectWithTerms,this@SubjectActivity)
                    subjectRecyclerView!!.adapter = subjectAdapter
                subjectRecyclerView!!.scheduleLayoutAnimation()


            }
        }
    }






    fun keyPressEvent(editText: EditText, ){





        editText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }

            override fun afterTextChanged(p0: Editable?) {
                offSearch = !editText.text.isNotEmpty()
                searchSubject( editText)

            }

        })


    }








    fun snackBar(v:View, str:String){

       Snackbar.make(v, str,Snackbar.LENGTH_LONG).setBackgroundTint(resources.getColor(R.color.black)).setTextColor(resources.getColor(R.color.white)).show()


    }








        private fun deleteButton(position: Int) : SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            this,
            "Delete",
            13.0f,
            android.R.color.holo_red_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    val builder = AlertDialog.Builder(this@SubjectActivity)

                    builder.setTitle("Delete Subject")
                    builder.setMessage("Are you sure you want to delete this subject?")

                    builder.setPositiveButton("Delete") { dialogue, which ->
                        _position = position + 1

                        deleteSubject(position)
                    }

                    builder.setNegativeButton("Cancel") {dialogue, which->


                    }

                    val dialouge : AlertDialog = builder.create()
                    dialouge.show()


                }
            })
    }

    private fun editButton(position: Int) : SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            this,
            "Edit",
            14.0f,
            android.R.color.holo_blue_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {

                    val name = subjectWithTerms[position].subject.name
                    val timeStamp =System.currentTimeMillis()
                    val subjectId = subjectWithTerms[position].subject.subjectId

                    _position=0

                    editSubject(
                        position,
                        name,
                        timeStamp,
                        subjectId
                    )



                }
            })
    }




    fun deleteSubject(index: Int){
        onSubjectDelete(index)
    }




    override fun onSubjectEdit(index: Int) {

    }




    override fun onSubjectDelete(index: Int) {




        var subjectId = subjectWithTerms[index].subject.subjectId


        CoroutineScope(Dispatchers.IO).launch {

            AppData.db.reviewerDao().deleteGroup(subjectId)



        }
        subjectWithTerms.removeAt(index)

       subjectAdapter!!.notifyItemRemoved(index)
      //  subjectAdapter!!.notifyDataSetChanged()




    }








}