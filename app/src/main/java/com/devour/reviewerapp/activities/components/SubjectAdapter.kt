package com.devour.reviewerapp.activities.components


import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devour.reviewerapp.R
import com.devour.reviewerapp.data.data_source.AppData
import com.devour.reviewerapp.data.relationship.SubjectWithTerms
import java.text.DateFormat

class SubjectAdapter (private val subjectWithTerms: MutableList<SubjectWithTerms>):
    RecyclerView.Adapter<SubjectViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val vh = SubjectViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent,false))

        return vh
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subjectWithTerms = subjectWithTerms[position]

        val shape = GradientDrawable()
        shape.cornerRadius = 40f
        shape.setColor(subjectWithTerms.subject.color)

        val textView:TextView = holder.itemView.findViewById(R.id.titleTextView)
        val main:LinearLayout = holder.itemView.findViewById(R.id.item_mainLayout)
        val timeStampTextView:TextView = holder.itemView.findViewById(R.id.timeStampTextView)

        val date = DateFormat.getDateTimeInstance().format(subjectWithTerms.subject.timeStamp)

        val text = holder.itemView.context.getString(R.string.subject_timestamp,date )
        timeStampTextView.text =text

        Log.i("sizeTag", "index $position, name: ${subjectWithTerms.subject.name }")
        textView.text = subjectWithTerms.subject.name
        main.setBackground(shape)


    }

    override fun getItemCount(): Int {
        return subjectWithTerms.size
    }
}