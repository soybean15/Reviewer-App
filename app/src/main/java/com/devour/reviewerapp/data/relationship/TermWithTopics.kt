package com.devour.reviewerapp.data.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.devour.reviewerapp.model.Term
import com.devour.reviewerapp.model.Topic
import java.io.Serializable

data class TermWithTopics (
    @Embedded val term: Term,
    @Relation (parentColumn = "termId" , entityColumn = "termId", entity = Topic::class)
    var topicWithItems :MutableList<TopicWithItems>
):Serializable