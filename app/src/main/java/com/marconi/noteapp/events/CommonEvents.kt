package com.marconi.noteapp.events

import com.marconi.noteapp.domain.model.Note
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class CommonEvents @Inject constructor() {
    sealed class Event {
        data class SaveNote(val saveNote: () -> Unit) : Event()
    }

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    suspend fun emitEvent(event: Event) {
        _events.emit(event)
    }
}