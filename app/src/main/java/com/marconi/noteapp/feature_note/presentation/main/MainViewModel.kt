package com.marconi.noteapp.feature_note.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marconi.noteapp.feature_note.presentation.util.ThemeManager
import com.marconi.noteapp.snackbar_utils.SnackbarController
import com.marconi.noteapp.snackbar_utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {
    private val _inDarkMode: MutableLiveData<Boolean?> =
        MutableLiveData(themeManager.getSavedTheme())
    val inDarkMode: LiveData<Boolean?> = _inDarkMode

    private val _saveNoteCallback = MutableLiveData<() -> Unit>()
    val saveNoteCallback: LiveData<() -> Unit> = _saveNoteCallback

    fun toggleDarkMode() {
        val current = _inDarkMode.value
        _inDarkMode.value = when (current) {
            true -> false
            false -> null
            null -> true
        }
        themeManager.setDarkThemeEnabled(_inDarkMode.value)
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