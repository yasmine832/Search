package com.example.finalsearch.data

import com.example.finalsearch.model.Word
import com.example.finalsearch.model.WordList
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {

    //word list operations

    /**
     * Get all word lists
     * Returns Flow
     */
    fun getAllWordLists(): Flow<List<WordList>> {
        return wordDao.getAllWordLists()
    }

    suspend fun getWordListById(id: Int): WordList? {
        return wordDao.getWordListById(id)
    }

    //Add new word list
    suspend fun addWordList(name: String, description: String = ""): Long {
        val wordList = WordList(
            name = name,
            description = description
        )
        return wordDao.insertWordList(wordList)
    }

    suspend fun deleteWordList(wordList: WordList) {
        wordDao.deleteWordList(wordList)
    }


    // word operations

    /**
     * Get all words in a specific list
     */
    fun getWordsForList(listId: Int): Flow<List<Word>> {
        return wordDao.getWordsForList(listId)
    }

    //Add word to list
    suspend fun addWord(listId: Int, wordText: String, definition: String) {
        val word = Word(
            wordListId = listId,
            wordText = wordText,
            definition = definition
        )
        wordDao.insertWord(word)
    }

    /**
     * Update word progress after practice
     * @param wasCorrect - Did user find the word correctly?
     */
    //TODO nadenken hoe puntjes verdelen
    suspend fun updateWordProgress(word: Word, wasCorrect: Boolean) {
        // Calculate new difficulty (0-5 scale)
        val newDifficulty = if (wasCorrect) {
            minOf(word.difficulty + 1, 5)  // Max  5 (mastered)
        } else {
            maxOf(word.difficulty - 1, 0)  // Min: 0 (struggling)
        }
 //zo? todo test en zie met dao
        val updatedWord = word.copy(
            timesShown = word.timesShown + 1,
            timesCorrect = if (wasCorrect) word.timesCorrect + 1 else word.timesCorrect,
            difficulty = newDifficulty,
            lastPracticed = System.currentTimeMillis()
        )

        wordDao.updateWord(updatedWord)
    }

    suspend fun deleteWord(word: Word) {
        wordDao.deleteWord(word)
    }


    // practice operations

    /**
     * Get words that need practice
     * Prioritize low difficulty (0-2)
     * Prioritize low success rate (<60%)
     */
    fun getWordsNeedingPractice(listId: Int, limit: Int = 10): Flow<List<Word>> {
        return wordDao.getWordsNeedingPractice(listId, limit)
    }
}