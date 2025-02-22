package com.marconi.noteapp.domain.use_case

import com.marconi.noteapp.domain.repository.DeleteNote

data class NoteUseCases(
    val getNotes: GetNotes,
    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val getNote: GetNote
)