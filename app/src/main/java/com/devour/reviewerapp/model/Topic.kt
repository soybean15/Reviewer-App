package com.devour.reviewerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Topic(
    var title:String,
    val termId:Int,
    val subjectId:Int,
    val timeStamp:Long,
    val color:Int
):Serializable {

    @PrimaryKey(autoGenerate = true)
    var topicId = 0

}