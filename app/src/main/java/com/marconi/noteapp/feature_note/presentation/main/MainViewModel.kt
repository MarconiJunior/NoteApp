package com.marconi.noteapp.feature_note.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marconi.noteapp.snackbar_utils.SnackbarController
import com.marconi.noteapp.snackbar_utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _inDarkMode = MutableLiveData(false)
    val inDarkMode: LiveData<Boolean> = _inDarkMode

    private val _saveNoteCallback = MutableLiveData<() -> Unit>()
    val saveNoteCallback: LiveData<() -> Unit> = _saveNoteCallback

    fun toggleDarkMode() {
        _inDarkMode.value = !(_inDarkMode.value ?: false)
    }

    fun setSaveNoteCallback(callback: () -> Unit) {
        _saveNoteCallback.value = callback
    }

    fun saveNote() {
        viewModelScope.launch {
            if (saveNoteCallback.value == null) {
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = "Save note callback is null"
                    )
                )
                return@launch
            }
            saveNoteCallback.value?.invoke()
        }
    }
}