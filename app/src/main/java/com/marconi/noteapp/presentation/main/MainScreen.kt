package com.marconi.noteapp.presentation.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.marconi.noteapp.R
import com.marconi.noteapp.events.CommonEvents
import com.marconi.noteapp.events.utils.ObserveAsEvents
import com.marconi.noteapp.presentation.add_edit_note.AddEditNoteScreen
import com.marconi.noteapp.presentation.notes.NotesScreen
import com.marconi.noteapp.presentation.util.Screen
import com.marconi.noteapp.snackbar_utils.SnackbarController
import com.marconi.noteapp.ui.theme.NoteAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    commonEvents: CommonEvents,
    viewModel: MainViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val navController = rememberNavController()
    val inDarkMode by viewModel.inDarkMode.observeAsState()
    val currentRoute = navController.currentBackStackEntryAsState()

    ObserveAsEvents(
        flow = commonEvents.events
    ) { event ->
        when (event) {
            is CommonEvents.Event.SaveNote -> {
                viewModel.setSaveNoteCallback(event.saveNote)
            }
        }
    }

    NoteAppTheme(
        darkTheme = when (inDarkMode) {
            true -> true
            false -> false
            null -> isSystemInDarkTheme()
        }
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface
        ) {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            if (
                                currentRoute.value?.destination?.route
                                    ?.contains(Screen.AddEditNoteScreen.route) == false
                            ) {
                                navController.navigate(Screen.AddEditNoteScreen.route)
                            } else {
                                viewModel.saveNote()
                            }
                        },
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector =
                            if (
                                currentRoute.value?.destination?.route
                                    ?.contains(Screen.AddEditNoteScreen.route) == false
                            ) {
                                Icons.Default.Add
                            } else Icons.Default.Save,
                            contentDescription = stringResource(R.string.add_note)
                        )
                    }
                },
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = stringResource(R.string.notes))
                        },
                        actions = {
                            IconButton(onClick = viewModel::toggleDarkMode) {
                                Crossfade(inDarkMode) { isDark ->
                                    when (isDark) {
                                        true -> {
                                            Icon(
                                                imageVector = Icons.Default.DarkMode,
                                                contentDescription =
                                                    stringResource(R.string.dark_mode)
                                            )
                                        }
                                        false -> {
                                            Icon(
                                                imageVector = Icons.Default.LightMode,
                                                contentDescription =
                                                    stringResource(R.string.light_mode)
                                            )
                                        }
                                        else -> {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription =
                                                    stringResource(R.string.system_default)
                                            )
                                        }
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
                    modifier = Modifier.padding(
                        PaddingValues(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding(),
                            start = 16.dp,
                            end = 16.dp
                        )
                    )
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