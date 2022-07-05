package com.devour.reviewerapp.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class Term (
    var name:String,
    var subjectId:Int,
    val timeStamp:Long,
    val color:Int
){
    @PrimaryKey(autoGenerate = true) var termId:Int = 0

}





