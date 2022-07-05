package com.devour.reviewerapp.data.data_source

import android.content.Context
import androidx.core.content.ContextCompat
import com.devour.reviewerapp.R
import com.devour.reviewerapp.model.*
import com.devour.reviewerapp.data.relationship.SubjectWithTerms
import com.devour.reviewerapp.data.relationship.TermWithTopics
import com.devour.reviewerapp.data.relationship.TopicWithItems

class AppData {

    companion object DataHolder{
        var dbFileName="reviewer_db"
        lateinit var db: ReviewerDatabase

        var subject: MutableList<SubjectWithTerms> = mutableListOf()

        fun initialize(context: Context){

            val subject1 = Subject("Subject1", System.currentTimeMillis(), ContextCompat.getColor(context, R.color.blue_1))
            val term1 = Term("Term1", subjectId = subject1.subjectId, timeStamp = System.currentTimeMillis(), subject1.color)
            val topic1 = Topic("Topic 1", term1.termId, term1.subjectId, timeStamp = System.currentTimeMillis(),subject1.color)
            val item = Item("Item 1", "Sample Description", topic1.topicId, topic1.termId, topic1.subjectId, timeStamp = System.currentTimeMillis(), color = subject1.color)

            val topicWithItems = TopicWithItems(topic1, mutableListOf(item))

            val termWithTopics = TermWithTopics(term1, mutableListOf(topicWithItems))

            val subjectWithTerms = SubjectWithTerms(subject1, mutableListOf(termWithTopics))

            subject = mutableListOf(subjectWithTerms)






        }
    }

}