package com.devour.reviewerapp.data.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.devour.reviewerapp.model.Subject
import com.devour.reviewerapp.model.Term
import java.io.Serializable

data class SubjectWithTerms (
    @Embedded val subject: Subject,
    @Relation (parentColumn = "subjectId", entityColumn = "subjectId", entity = Term::class)val termsWithTopics: MutableList<TermWithTopics>
        ):Serializable