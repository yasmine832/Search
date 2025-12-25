package com.example.finalsearch.data

import androidx.room.*
import com.example.finalsearch.model.Word
import com.example.finalsearch.model.WordList
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    // Wordlist operations

    /**
     * Get all word lists
     * Flow = automatic UI updates when data changes !     */
    @Query("SELECT * FROM word_lists ORDER BY createdAt DESC")
    fun getAllWordLists(): Flow<List<WordList>>

    @Query("SELECT * FROM word_lists WHERE id = :id")
    suspend fun getWordListById(id: Int): WordList?

    @Insert
    suspend fun insertWordList(wordList: WordList): Long

    @Delete
    suspend fun deleteWordList(wordList: WordList)


    // Word operations

    /**
     * Get all words for a specific word list
     * Ordered by difficulty (difficulty first)
     */
    @Query("SELECT * FROM words WHERE wordListId = :listId ORDER BY difficulty ASC")
    fun getWordsForList(listId: Int): Flow<List<Word>>

    @Insert
    suspend fun insertWord(word: Word)

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)


    // Game operations

    //Get words that need practice

    //todo sourceee link
    @Query("""
        SELECT * FROM words 
        WHERE wordListId = :listId 
        AND (difficulty < 3 OR (timesCorrect * 100.0 / NULLIF(timesShown, 0)) < 60) 
        ORDER BY difficulty ASC, lastPracticed ASC
        LIMIT :limit
    """)
    fun getWordsNeedingPractice(listId: Int, limit: Int = 10): Flow<List<Word>>
}