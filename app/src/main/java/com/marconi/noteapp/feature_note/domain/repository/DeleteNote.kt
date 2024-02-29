package com.marconi.noteapp.feature_note.domain.repository

import com.marconi.noteapp.feature_note.domain.model.Note

class DeleteNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}