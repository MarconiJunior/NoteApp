package com.marconi.noteapp.feature_note.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _inDarkMode = MutableLiveData(false)
    val inDarkMode: LiveData<Boolean> = _inDarkMode

    fun toggleDarkMode() {
        _inDarkMode.value = !(_inDarkMode.value ?: false)
    }
}