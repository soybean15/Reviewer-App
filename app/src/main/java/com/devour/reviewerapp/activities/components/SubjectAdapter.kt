package com.devour.reviewerapp.activities.components


import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.activities.SubjectActivity
import com.devour.reviewerapp.data.data_source.AppData
import com.devour.reviewerapp.data.relationship.SubjectWithTerms
import com.devour.reviewerapp.data.relationship.TermWithTopics
import com.devour.reviewerapp.model.Subject
import java.text.DateFormat
import kotlin.contracts.contract


interface OnSubjectClickListener{
    fun onSubjectEdit(index: Int)
    fun onSubjectDelete(index: Int)
    fun onSubjectClick(subjectWithTerms:SubjectWithTerms)
    fun onAnimateSubjectClick(view: View,index: Int){

    }
}

class SubjectAdapter (private val subjectWithTerms: MutableList<SubjectWithTerms>,listenerContext: OnSubjectClickListener):
    RecyclerView.Adapter<SubjectViewHolder>(){

    private var myInterface:OnSubjectClickListener = listenerContext




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val vh = SubjectViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent,false))

        return vh
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subjectWithTerms = subjectWithTerms[position]


        holder.bind(subjectWithTerms)

        myInterface.onAnimateSubjectClick(holder.itemView,position)

        holder.itemView.setOnClickListener {
            myInterface.onSubjectClick(subjectWithTerms)
        }




    }




    override fun getItemCount(): Int {
        return subjectWithTerms.size
    }







}