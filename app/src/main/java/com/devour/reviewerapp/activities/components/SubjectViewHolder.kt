package com.devour.reviewerapp.activities.components

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.data.relationship.SubjectWithTerms
import java.text.DateFormat

class SubjectViewHolder(inflater: View ) : RecyclerView.ViewHolder(inflater){
    private var titleTextView: TextView? = null
    private var mainLayout: LinearLayout? = null
    private var timeStampTextView:TextView?=null

    init {

        titleTextView = itemView.findViewById(R.id.titleTextView)
        mainLayout = itemView.findViewById(R.id.item_mainLayout)
        timeStampTextView = itemView.findViewById(R.id.timeStampTextView)
    }

    fun bind(subjectWithTerms: SubjectWithTerms){




        val shape = GradientDrawable()

        shape.cornerRadius = 40f
        shape.setColor(subjectWithTerms.subject.color)

        val textView:TextView = itemView.findViewById(R.id.titleTextView)
        val main:LinearLayout = itemView.findViewById(R.id.item_mainLayout)
        val timeStampTextView:TextView = itemView.findViewById(R.id.timeStampTextView)

        val date = DateFormat.getDateTimeInstance().format(subjectWithTerms.subject.timeStamp)

        val text = itemView.context.getString(R.string.subject_timestamp,date )
        timeStampTextView.text =text


        textView.text = subjectWithTerms.subject.name
        main.background= shape


    }

}

