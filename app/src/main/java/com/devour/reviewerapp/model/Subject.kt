package com.devour.reviewerapp.model

import android.content.res.Resources
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.devour.reviewerapp.R

@Entity
data class Subject (var name: String,val timeStamp:Long,val color:Int){
        @PrimaryKey(autoGenerate = true)
        var subjectId: Int = 0





}
class EmptyFieldException(message:String):Exception(message)


