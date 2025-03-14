package com.marconi.noteapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.marconi.noteapp.events.CommonEvents
import com.marconi.noteapp.events.utils.ObserveAsEvents
import com.marconi.noteapp.presentation.main.MainScreen
import com.marconi.noteapp.snackbar_utils.SnackbarController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var commonEvents: CommonEvents
    @Inject lateinit var snackbarController: SnackbarController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            ObserveAsEvents(
                flow = snackbarController.events,
                snackbarHostState
            ) { event ->
                lifecycleScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()

                    val result = snackbarHostState.showSnackbar(
                        message = if (event.message is Int) {
                            getString(event.message)
                        } else {
                            event.message.toString()
                        },
                        actionLabel = event.action?.name,
                        duration = SnackbarDuration.Long
                    )

                    if(result == SnackbarResult.ActionPerformed) {
                        event.action?.action?.invoke()
                    }
                }
            }
            MainScreen(
                commonEvents,
                snackbarHostState = snackbarHostState
            )
        }
    }
}