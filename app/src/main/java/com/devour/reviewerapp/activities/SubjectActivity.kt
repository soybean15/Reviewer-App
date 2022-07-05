package com.devour.reviewerapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager

import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginStart
import androidx.core.view.setMargins

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.activities.components.SubjectAdapter
import com.devour.reviewerapp.data.data_source.AppData
import com.devour.reviewerapp.data.data_source.ReviewerDatabase
import com.devour.reviewerapp.data.relationship.SubjectWithTerms
import com.devour.reviewerapp.model.EmptyFieldException
import com.devour.reviewerapp.model.Subject
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception


class SubjectActivity : AppCompatActivity() {

    private var subjectAdapter:SubjectAdapter? = null





    var _hasFocus = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        val subjectRecyclerView:RecyclerView = findViewById(R.id.subjectRecyclerView)


        val layoutManager:RecyclerView.LayoutManager = LinearLayoutManager(this, )

        subjectRecyclerView.layoutManager =layoutManager




        AppData.initialize(this)
        subjectAdapter = SubjectAdapter(AppData.subject )
        subjectRecyclerView.adapter = subjectAdapter

        AppData.db = ReviewerDatabase.getDataBase(this)



        dbConnect(subjectRecyclerView)

        searchItem(subjectRecyclerView)






    }



    private fun databaseFileExist():Boolean{
        return try {
            File(getDatabasePath(AppData.dbFileName).absolutePath).exists()
        }catch (e: Exception){
            false
        }
    }


    fun createSubject(v: View){
        val subjectRecyclerView:RecyclerView = findViewById(R.id.subjectRecyclerView)

        val builder = AlertDialog.Builder(this)


        builder.setTitle("New Subject")
        builder.setMessage("Enter Subject Name")


        val myInput = EditText(this)
        myInput.inputType = InputType.TYPE_CLASS_TEXT

        builder.setView(myInput)

        builder.setPositiveButton("Save") {dialogue, which->

            val subjectName:String = myInput.text.toString()

            if (subjectName.isBlank()) {
                snackBar(v,"Subject Name is Empty")

            }else{




                val newSubject = Subject(subjectName,System.currentTimeMillis(), resources.getIntArray(R.array.random_colors).random())
                val newSubjectWithTerms = SubjectWithTerms(newSubject, mutableListOf())

                AppData.subject.add(newSubjectWithTerms)

                CoroutineScope(Dispatchers.IO).launch {

                    AppData.db.reviewerDao().insertSubject(newSubject)
                    AppData.subject.sortByDescending { it.subject.timeStamp }

                    withContext(Dispatchers.Main){
                        Log.i("sizeTag",AppData.subject.get(AppData.subject.size-1).subject.name)


                        subjectAdapter = SubjectAdapter(AppData.subject)
                        subjectRecyclerView.adapter = subjectAdapter


                        //subjectAdapter!!.notifyItemInserted(AppData.subject.count())
                      //  subjectRecyclerView.scrollToPosition(0)
                    }


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

    private fun dbConnect(recyclerView: RecyclerView){

        if(databaseFileExist()){//read content
            loadSubjects(recyclerView)

        }else{//first Time opening the app
            AppData.initialize(this)
            subjectAdapter = SubjectAdapter(AppData.subject)
            recyclerView.adapter = subjectAdapter

            CoroutineScope(Dispatchers.IO).launch {
                for (groupWithItems in AppData.subject) {
                    AppData.db.reviewerDao().insertSubject(groupWithItems.subject)


                }
            }
        }
    }


    fun loadSubjects(recyclerView: RecyclerView){
        CoroutineScope(Dispatchers.IO).launch {
            AppData.subject =AppData.db.reviewerDao().getSubjectWithTerms()
            AppData.subject.sortByDescending { it.subject.timeStamp }

            withContext(Dispatchers.Main){
                subjectAdapter = SubjectAdapter(AppData.subject)
                recyclerView.adapter = subjectAdapter
            }
        }
    }

    fun searchItem( recyclerView:RecyclerView){
        val searchTextView:EditText = findViewById(R.id.searchTextView)

        backSpaceEvent(searchTextView)

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



                    searchSubject(searchTextView, recyclerView)



                    val inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(view.windowToken,0)

                }
            }
            false
        }




    }

    fun searchSubject( text: EditText, recyclerView: RecyclerView){
        val name :String = text.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            val subjectWithTerms = AppData.db.reviewerDao().getSubjects(name)
            withContext(Dispatchers.Main){
                if(subjectWithTerms.size==0){
                    snackBar(recyclerView, "No Item Found")
                }else{
                    subjectWithTerms.sortByDescending { it.subject.timeStamp }
                    subjectAdapter = SubjectAdapter(subjectWithTerms)
                    recyclerView.adapter = subjectAdapter
                }

            }
        }
    }









//    fun saveColors(){
//        val color = resources.obtainTypedArray(R.array.random_colors).use{ta->
//            IntArray(ta.length()){
//                ta.getColor(it,0)
//            }
//
//        }
//
//
//    }


    fun backSpaceEvent(editText: EditText, ){

        val subjectRecyclerView:RecyclerView = findViewById(R.id.subjectRecyclerView)



        editText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchSubject( editText, subjectRecyclerView)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })



//       view.setOnKeyListener { _, keyCode, keyEvent ->
//
//           if(keyCode == KeyEvent.KEYCODE_DEL){
//
//
//               if(view.text.isEmpty()){
//                   loadSubjects(subjectRecyclerView)
//                   snackBar(view,"TextField is empty")
//               }
//
//
//           }
//           false
//       }
    }









    fun snackBar(v:View, str:String){

       Snackbar.make(v, str,Snackbar.LENGTH_LONG).setBackgroundTint(resources.getColor(R.color.black)).setTextColor(resources.getColor(R.color.white)).show()


    }


}