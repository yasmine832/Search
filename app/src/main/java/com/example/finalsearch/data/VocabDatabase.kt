package com.example.finalsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finalsearch.model.Word
import com.example.finalsearch.model.WordList

/**
 * Room Database instance */
 @Database(
    entities = [Word::class, WordList::class],
    version = 1,
    exportSchema = false
)
abstract class VocabDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: VocabDatabase? = null

        fun getDatabase(context: Context): VocabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabDatabase::class.java,
                    "vocab_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}