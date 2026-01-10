package com.example.finalsearch.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WordList entity is the Collection of related vocabulary words
 */
@Entity(tableName = "word_lists")
data class WordList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,                   // todoo e.g., "dutch animals"

    val description: String = "",       // Optional defintion/descirpotion e.G

    val createdAt: Long = System.currentTimeMillis()
)

