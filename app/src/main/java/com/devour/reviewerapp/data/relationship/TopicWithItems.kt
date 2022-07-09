package com.devour.reviewerapp.data.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.devour.reviewerapp.model.Item
import com.devour.reviewerapp.model.Topic
import java.io.Serializable

data class TopicWithItems (
    @Embedded val topic: Topic,
    @Relation(parentColumn = "topicId", entityColumn = "topicId")val items:MutableList<Item>

):Serializable