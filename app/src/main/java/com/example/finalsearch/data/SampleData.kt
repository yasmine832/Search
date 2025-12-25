package com.example.finalsearch.data

import com.example.finalsearch.model.Word
import com.example.finalsearch.model.WordList

object SampleData {

    /**
     * Sample word lists (eng§dutch)
     */
    suspend fun populateDatabase(repository: WordRepository) {
        // Word list 1: Basic Dutch
        val basicDutchId = repository.addWordList(
            name = "Dutch Basics",
            description = "Essential Dutch words"
        ).toInt()

        // Add words to "Dutch Basics"
        repository.addWord(basicDutchId, "hallo", "hello")
        repository.addWord(basicDutchId, "dag", "goodbye")
        repository.addWord(basicDutchId, "dank je wel", "thank you")
        repository.addWord(basicDutchId, "alsjeblieft", "please / you're welcome")
        repository.addWord(basicDutchId, "ja", "yes")
        repository.addWord(basicDutchId, "nee", "no")

        // 2: Dutch Animals
        val animalsId = repository.addWordList(
            name = "Dutch Animals",
            description = "animal names in Dutch"
        ).toInt()

        repository.addWord(animalsId, "kat", "cat")
        repository.addWord(animalsId, "hond", "dog")
        repository.addWord(animalsId, "vogel", "bird")
        repository.addWord(animalsId, "vis", "fish")
        repository.addWord(animalsId, "paard", "horse")

        // 3: Dutch Food
        val foodId = repository.addWordList(
            name = "Dutch Food & Drinks",
            description = "Food vocabulary"
        ).toInt()

        repository.addWord(foodId, "brood", "bread")
        repository.addWord(foodId, "kaas", "cheese")
        repository.addWord(foodId, "melk", "milk")
        repository.addWord(foodId, "water", "water")
        repository.addWord(foodId, "koffie", "coffee")
    }
}