package me.cares.securityexam.application.domain

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("ANSWER")
class Answer private constructor(
    content: String,
    expert: Account,
) : Post(
    content = content,
    writer = expert,
) {

    companion object {

        fun createNewAnswer(
            content: String,
            expert: Account,
        ): Answer {
            return Answer(
                content = content,
                expert = expert,
            )
        }

    }

}