package com.minotawr.storyapp.utils

import com.minotawr.storyapp.domain.model.Story

object DataDummy {

    fun generateDummyUseCaseData(): List<Story> {
        val result = mutableListOf<Story>()
        for (i in 0..100) {
            val story = Story(
                id = "$i",
                name = "User $i",
                description = "Description $i",
                photoUrl = null,
                createdAt = "2023-10-13T10:10:10Z",
                latitude = 6.194345596425415,
                longitude = 106.81946638562397
            )

            result.add(story)
        }

        return result
    }

}