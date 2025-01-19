package com.marconi.noteapp.feature_note.presentation.notes

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.marconi.noteapp.feature_note.presentation.notes.components.NoteItem
import com.marconi.noteapp.feature_note.presentation.notes.components.OrderSection
import com.marconi.noteapp.feature_note.presentation.util.Screen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val showDialog by viewModel.showDialog.observeAsState(false)
    val state = viewModel.state.value
    if (showDialog) {
        DeleteConfirmDialog()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your note",
                style = MaterialTheme.typography.titleSmall
            )
            IconButton(
                onClick = {
                    viewModel.onEvent(NotesEvent.ToggleOrderSection)
                },
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
            }
        }
        AnimatedVisibility(
            visible = state.isOrderSectionVisible,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            OrderSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                noteOrder = state.noteOrder,
                onOrderChange = {
                    viewModel.onEvent(NotesEvent.Order(it))
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.notes) { note ->
                NoteItem(
                    note = note,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(
                                Screen.AddEditNoteScreen.route +
                                    "?noteId=${note.id}&noteColor=${note.color}"
                            )
                        },
                    onDeleteClick = {
                        viewModel.setCurrentSelectedNote(note)
                        viewModel.toggleDialogVisibility()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(viewModel: NotesViewModel = hiltViewModel()) {
    AlertDialog(
        onDismissRequest = viewModel::toggleDialogVisibility,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Delete note")
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Delete note"
                )
            }
        },
        text = { Text(text = "Are you sure you want to delete this note?") },
        confirmButton = {
            IconButton(onClick = viewModel::deleteNote) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            IconButton(onClick = viewModel::toggleDialogVisibility) {
                Text(text = "No")
            }
        },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}