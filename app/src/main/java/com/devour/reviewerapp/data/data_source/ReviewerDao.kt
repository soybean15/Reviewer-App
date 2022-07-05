package com.devour.reviewerapp.data.data_source

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.devour.reviewerapp.model.Item
import com.devour.reviewerapp.model.Subject

import com.devour.reviewerapp.model.Term
import com.devour.reviewerapp.model.Topic
import com.devour.reviewerapp.data.relationship.SubjectWithTerms
import com.devour.reviewerapp.data.relationship.TermWithTopics
import com.devour.reviewerapp.data.relationship.TopicWithItems


@Dao
interface ReviewerDao {


    @Insert
    fun insertSubject(subject: Subject)

    @Insert
    fun insertTerms(term: Term)

    @Insert
    fun insertTopic(topic: Topic)

    @Insert
    fun insertItem(item: Item)

    @Transaction
    @Query("Select * from Subject")
    fun getSubjectWithTerms(): MutableList<SubjectWithTerms>

    @Query("Select * from Subject where name like '%' || :name || '%'")
    fun getSubjects(name:String): MutableList<SubjectWithTerms>


    @Transaction
    @Query("Select * from Term")
    fun getTermWithTopic(): MutableList<TermWithTopics>

    @Transaction
    @Query("Select * from Topic")
    fun getTopicWithItem(): MutableList<TopicWithItems>


}