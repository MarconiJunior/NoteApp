package com.marconi.noteapp.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godaddy.android.colorpicker.HsvColor
import com.marconi.noteapp.R
import com.marconi.noteapp.events.CommonEvents
import com.marconi.noteapp.domain.model.InvalidNoteException
import com.marconi.noteapp.domain.model.Note
import com.marconi.noteapp.domain.use_case.NoteUseCases
import com.marconi.noteapp.snackbar_utils.SnackbarController
import com.marconi.noteapp.snackbar_utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val commonEvents: CommonEvents,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _noteTitle = mutableStateOf(
        NoteTextFieldState(
        hint = R.string.enter_title
    )
    )
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(
        NoteTextFieldState(
        hint = R.string.enter_some_content
    )
    )
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _selectedCustomColor = MutableLiveData<Color?>(null)
    val selectedCustomColor: LiveData<Color?> = _selectedCustomColor

    private val _isColorDialogVisible = MutableLiveData(false)
    val isColorDialogVisible: LiveData<Boolean> = _isColorDialogVisible

    private val _isFontDialogVisible = MutableLiveData(false)
    val isFontDialogVisible: LiveData<Boolean> = _isFontDialogVisible

    private val _noteColor = mutableStateOf(Note.noteColors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    private val _fontSize = MutableLiveData(16f)
    val fontSize: LiveData<Float> = _fontSize

    private val _textColor = MutableLiveData(Color.Black.toArgb())
    val textColor: LiveData<Int> = _textColor

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    private var currentNoteId: Int? = null


    init {
        saveNote()
        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            if(noteId != -1) {
                viewModelScope.launch {
                    noteUseCases.getNote(noteId)?.also { note ->
                        currentNoteId = note.id
                        _noteTitle.value = noteTitle.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteContent.value = _noteContent.value.copy(
                            text = note.content,
                            isHintVisible = false
                        )
                        onEvent(AddEditNoteEvent.ChangeColor(note.color))
                        onEvent(AddEditNoteEvent.ChangeFontSize(note.fontSize))
                        onEvent(AddEditNoteEvent.ChangeTextColor(note.textColor))
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitle.value = noteTitle.value.copy(
                    text = event.value
                )
            }
            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            noteTitle.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.EnteredContent -> {
                _noteContent.value = _noteContent.value.copy(
                    text = event.value
                )
            }
            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteContent.value = _noteContent.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            _noteContent.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = event.color
            }
            is AddEditNoteEvent.ChangeFontSize -> {
                _fontSize.value = event.fontSize
            }
            is AddEditNoteEvent.ChangeTextColor -> {
                _textColor.value = event.textColor
            }
            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.addNote(
                            Note(
                                title = noteTitle.value.text,
                                content = noteContent.value.text,
                                timestamp = System.currentTimeMillis(),
                                color = noteColor.value,
                                textColor = textColor.value ?: Color.Black.toArgb(),
                                fontSize = fontSize.value ?: 16f,
                                id = currentNoteId
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveNote)
                    } catch(e: InvalidNoteException) {
                        emmitSnackbar(e.message)
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data object SaveNote: UiEvent()
    }

    private fun saveNote() {
        viewModelScope.launch {
            commonEvents.emitEvent(CommonEvents.Event.SaveNote {
                onEvent(AddEditNoteEvent.SaveNote)
            })
        }
    }

    private suspend fun emmitSnackbar(message: String?) {
        snackbarController.sendEvent(
            SnackbarEvent(
                message = message ?: R.string.couldn_t_save_note
            )
        )
    }

    fun setSelectedCustomColor(hsvColor: HsvColor) {
        _selectedCustomColor.value = hsvColor.toColor()
    }

    fun toggleColorDialogVisibility() {
        _isColorDialogVisible.value = !(isColorDialogVisible.value ?: false)
    }

    fun toggleFontDialogVisibility() {
        _isFontDialogVisible.value = !(isFontDialogVisible.value ?: false)
    }
}