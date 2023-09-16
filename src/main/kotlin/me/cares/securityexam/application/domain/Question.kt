package me.cares.securityexam.application.domain

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("QUESTION")
class Question private constructor(
    content: String,
    writer: Account,
) : Post(
    content = content,
    writer = writer,
) {

    companion object {

        fun createNewQuestion(
            content: String,
            writer: Account,
        ): Question {

            return Question(
                content = content,
                writer = writer
            )

        }

    }

}