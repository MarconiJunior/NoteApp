package com.marconi.noteapp.feature_note.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marconi.noteapp.ui.theme.BabyBlue
import com.marconi.noteapp.ui.theme.RedOrange
import com.marconi.noteapp.ui.theme.RedPink
import com.marconi.noteapp.ui.theme.Violet

@Entity
data class Note(
    val title: String,
    val content: String,
    val timestamp: Long,
    val color: Int,
    @PrimaryKey val id: Int? = null
) {
    companion object {
        val noteColors = listOf(RedOrange, LightGray, Violet, BabyBlue, RedPink)
    }
}

class InvalidNoteException(message: String): Exception(message)
