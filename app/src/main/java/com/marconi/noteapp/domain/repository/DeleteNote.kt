package com.marconi.noteapp.domain.repository

import com.marconi.noteapp.domain.model.Note

class DeleteNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}