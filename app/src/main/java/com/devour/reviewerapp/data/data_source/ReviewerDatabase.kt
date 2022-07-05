package com.devour.reviewerapp.data.data_source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.devour.reviewerapp.model.Item
import com.devour.reviewerapp.model.Subject
import com.devour.reviewerapp.model.Term
import com.devour.reviewerapp.model.Topic


@Database(entities = [Subject::class, Term::class,Topic::class, Item::class], version = 1)
abstract class ReviewerDatabase :RoomDatabase() {
    abstract fun reviewerDao() : ReviewerDao



    companion object{

        @Volatile
        private var INSTANCE : ReviewerDatabase? =null
        fun getDataBase(context: Context):ReviewerDatabase{
            val tempInstance = INSTANCE

            if(tempInstance != null){return tempInstance}

            synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,ReviewerDatabase::class.java,AppData.dbFileName).build()

                INSTANCE=instance
                return instance
            }
        }
    }




}