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
    fun insertSubject(subject: Subject):Long

    @Insert
    fun insertTerms(term: Term):Long

    @Insert
    fun insertTopic(topic: Topic):Long

    @Insert
    fun insertItem(item: Item)

    @Transaction
    @Query("Select * from Subject")
    fun getSubjectWithTerms(): MutableList<SubjectWithTerms>



    @Query("Select * from Subject where name like '%' || :name || '%'")
    fun getSubjects(name:String): MutableList<SubjectWithTerms>


    @Query("Select * from Subject where subjectId=:subjectId")
    fun getSubjectsById(subjectId: Int): SubjectWithTerms


    @Transaction
    @Query("Select * from Term where subjectId =:subjectId")
    fun getTermsWithTopic(subjectId:Int): MutableList<TermWithTopics>

    @Transaction
    @Query("Select * from Topic where termId=:termId")
    fun getTopicWithItem(termId:Int): MutableList<TopicWithItems>

    @Query("Delete from Subject where subjectId=:subjectId")
    fun deleteGroup(subjectId: Int)

    @Query("Update Subject set name=:name, timeStamp =:timeStamp where subjectId=:subjectId")
    fun updateSubject(name: String, timeStamp:Long, subjectId:Int)

    @Query("Select * from Item where subjectId =:subjectId")
    fun getAllItems(subjectId: Int):MutableList<Item>






}