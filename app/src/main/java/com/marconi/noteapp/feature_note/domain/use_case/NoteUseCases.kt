package com.marconi.noteapp.feature_note.domain.use_case

import com.marconi.noteapp.feature_note.domain.repository.DeleteNote

data class NoteUseCases(
    val getNotes: GetNotes,
    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val getNote: GetNote
)