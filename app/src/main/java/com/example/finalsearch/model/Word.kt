package com.example.finalsearch.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Word entity representing a vocabulary word
 *Represents the data structure
 * and Room automatically creates database table from this
 *
 * https://developer.android.com/training/data-storage/room/defining-data
 */
@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,                    // Auto-generated unique ID

    val wordListId: Int,                // Which list this word belongs to

    val wordText: String,               // The vocabulary word (e.g., "gato")

    val definition: String,             // Definition/translation (e.g., "cat")

    // TRACKING
    val difficulty: Int = 0,            // 0-5 scale (0=new, 5=mastered)

    val timesShown: Int = 0,            // How many times appeared in game

    val timesCorrect: Int = 0,          // How many times found correctly

    val lastPracticed: Long = 0L        // Timestamp for spaced repetition
) {
    /**
     * Calculate success rate percentage FOR SPACED repetition
     * source: TODOO
     */
    val successRate: Float
        get() = if (timesShown > 0) {
            (timesCorrect.toFloat() / timesShown) * 100f
        } else {
            0f
        }

    fun needsPractice(): Boolean {
        return difficulty < 3 || successRate < 60f
    }
}