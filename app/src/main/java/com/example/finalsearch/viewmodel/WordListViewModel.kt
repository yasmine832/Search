package com.example.finalsearch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalsearch.data.VocabDatabase
import com.example.finalsearch.data.WordRepository
import com.example.finalsearch.model.Word
import com.example.finalsearch.model.WordList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// for managing word lists and words

class WordListViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize repository with database
    private val repository: WordRepository

    // Expose data as Flow so UI observes these
    val allWordLists: Flow<List<WordList>>

    init {
        // Get database instance
        val database = VocabDatabase.getDatabase(application)
        val wordDao = database.wordDao()

        // Create repository
        repository = WordRepository(wordDao)

        // Get word lists
        allWordLists = repository.getAllWordLists()
    }

    //operations for wordlistq

    fun addWordList(name: String, description: String = "") {
        viewModelScope.launch {  // in background
            repository.addWordList(name, description)
        }
    } // !!!

    fun deleteWordList(wordList: WordList) {
        viewModelScope.launch {
            repository.deleteWordList(wordList)
        }
    }

    // operatiosn fro the words

    fun getWordsForList(listId: Int): Flow<List<Word>> {
        return repository.getWordsForList(listId)
    }

    fun addWord(listId: Int, wordText: String, definition: String) {
        viewModelScope.launch {
            repository.addWord(listId, wordText, definition)
        }
    }

    fun updateWordProgress(word: Word, wasCorrect: Boolean) {
        viewModelScope.launch {
            repository.updateWordProgress(word, wasCorrect)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            repository.deleteWord(word)
        }
    }
}