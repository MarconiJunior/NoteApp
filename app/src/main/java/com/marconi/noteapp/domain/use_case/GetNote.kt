package com.marconi.noteapp.domain.use_case

import com.marconi.noteapp.domain.model.Note
import com.marconi.noteapp.domain.repository.NoteRepository

class GetNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(id: Int): Note? {
        return repository.getNoteById(id)
    }
}