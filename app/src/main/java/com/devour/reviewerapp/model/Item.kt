package com.devour.reviewerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class Item (
    var title:String,
    var desc: String,
    val topicId:Int,
    val termId : Int,
    val subjectId: Int,
    val timeStamp:Long,
    val color:Int
){
    @PrimaryKey(autoGenerate = true)
    var itemId = 0
}