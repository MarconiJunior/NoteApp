package com.marconi.noteapp.feature_note.presentation.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.marconi.noteapp.events_utils.ObserveAsEvents
import com.marconi.noteapp.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.marconi.noteapp.feature_note.presentation.notes.NotesScreen
import com.marconi.noteapp.feature_note.presentation.util.Screen
import com.marconi.noteapp.snackbar_utils.SnackbarController
import com.marconi.noteapp.ui.theme.NoteAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val inDarkMode by viewModel.inDarkMode.observeAsState()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()
    ObserveAsEvents(
        flow = SnackbarController.events,
        snackbarHostState
    ) { event ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name,
                duration = SnackbarDuration.Long
            )

            if(result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }
    NoteAppTheme(
        darkTheme =  inDarkMode ?: isSystemInDarkTheme()
    ) {
        val navController = rememberNavController()
        Surface(
            color = MaterialTheme.colorScheme.surface
        ) {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(Screen.AddEditNoteScreen.route)
                        },
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add note")
                    }
                },
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "Notes")
                        },
                        actions = {
                            IconButton(onClick = viewModel::toggleDarkMode) {
                                Crossfade(inDarkMode) { isDark ->
                                    if (isDark == true) {
                                        Icon(
                                            imageVector = Icons.Default.DarkMode,
                                            contentDescription = "Dark Mode"
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.LightMode,
                                            contentDescription = "Light Mode"
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.NotesScreen.route,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable(route = Screen.NotesScreen.route) {
                        NotesScreen(navController = navController)
                    }
                    composable(
                        route = Screen.AddEditNoteScreen.route +
                                "?noteId={noteId}&noteColor={noteColor}",
                        arguments = listOf(
                            navArgument(
                                name = "noteId"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                            navArgument(
                                name = "noteColor"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                        )
                    ) {
                        val color = it.arguments?.getInt("noteColor") ?: -1
                        AddEditNoteScreen(
                            navController = navController,
                            noteColor = color
                        )
                    }
                }
            }
        }
    }
}