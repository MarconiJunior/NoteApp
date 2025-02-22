package com.marconi.noteapp.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marconi.noteapp.R
import com.marconi.noteapp.domain.model.Note
import com.marconi.noteapp.domain.use_case.NoteUseCases
import com.marconi.noteapp.domain.util.NoteOrder
import com.marconi.noteapp.domain.util.OrderType
import com.marconi.noteapp.snackbar_utils.SnackbarAction
import com.marconi.noteapp.snackbar_utils.SnackbarController
import com.marconi.noteapp.snackbar_utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {
    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    private val _showDialog = MutableLiveData(false)
    val showDialog: MutableLiveData<Boolean> = _showDialog

    private val _currentSelectedNote = MutableLiveData<Note?>(null)
    val currentSelectedNote: MutableLiveData<Note?> = _currentSelectedNote

    private var recentlyDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (
                    state.value.noteOrder::class == event.noteOrder::class &&
                    state.value.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    return
                }
                getNotes(event.noteOrder)
            }
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotes(noteOrder)
            .onEach { notes ->
                _state.value = state.value.copy(
                    notes = notes,
                    noteOrder = noteOrder
                )
            }
            .launchIn(viewModelScope)
    }

    fun setCurrentSelectedNote(note: Note?) {
        _currentSelectedNote.value = note
    }

    fun toggleDialogVisibility() {
        _showDialog.value = !(showDialog.value ?: false)
    }

    fun deleteNote() {
        viewModelScope.launch {
            currentSelectedNote.value?.let { note ->
                onEvent(NotesEvent.DeleteNote(note))
                toggleDialogVisibility()
                setCurrentSelectedNote(null)
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = R.string.note_deleted,
                        action = SnackbarAction(
                            "Undo",
                            action = { onEvent(NotesEvent.RestoreNote) }
                        )
                    )
                )
            }
        }
    }
}