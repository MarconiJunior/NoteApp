package com.marconi.noteapp.presentation.add_edit_note

import androidx.annotation.StringRes

data class NoteTextFieldState(
    val text: String = "",
    @StringRes val hint: Int? = null,
    val isHintVisible: Boolean = true
)
