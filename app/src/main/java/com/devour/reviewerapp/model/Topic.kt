package com.devour.reviewerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Topic(
    var title:String,
    val termId:Int,
    val subjectId:Int,
    val timeStamp:Long,
    val color:Int
) {

    @PrimaryKey(autoGenerate = true)
    var topicId = 0

}