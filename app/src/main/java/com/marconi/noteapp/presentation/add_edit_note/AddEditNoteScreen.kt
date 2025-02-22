package com.marconi.noteapp.presentation.add_edit_note

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.godaddy.android.colorpicker.toColorInt
import com.marconi.noteapp.R
import com.marconi.noteapp.domain.model.Note
import com.marconi.noteapp.presentation.add_edit_note.components.TransparentHintTextField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteColor: Int,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value
    val selectedCustomColor by viewModel.selectedCustomColor.observeAsState()
    val isDialogVisible by viewModel.isDialogVisible.observeAsState(false)

    val noteBackgroundAnimatable = remember {
        Animatable(
            Color(if (noteColor != -1) noteColor else viewModel.noteColor.value)
        )
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
            }
        }
    }

    if (isDialogVisible) {
        ColorPickerDialog(
            onColorChanged = { color ->
                color.toColorInt().let {
                    scope.launch {
                        noteBackgroundAnimatable.animateTo(
                            targetValue = Color(it),
                            animationSpec = tween(
                                durationMillis = 500
                            )
                        )
                    }
                    viewModel.onEvent(AddEditNoteEvent.ChangeColor(it))
                }
                viewModel.setSelectedCustomColor(color)
            },
            onDismissRequest = {
                viewModel.toggleDialogVisibility()
            },
            color = HsvColor.from(selectedCustomColor ?: Color(viewModel.noteColor.value))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(noteBackgroundAnimatable.value, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Note.noteColors.forEach { color ->
                val colorInt = color.toArgb()
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(15.dp, CircleShape)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = 3.dp,
                            color = if (viewModel.noteColor.value == colorInt) {
                                MaterialTheme.colorScheme.primary
                            } else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable {
                            scope.launch {
                                noteBackgroundAnimatable.animateTo(
                                    targetValue = Color(colorInt),
                                    animationSpec = tween(
                                        durationMillis = 500
                                    )
                                )
                            }
                            viewModel.onEvent(AddEditNoteEvent.ChangeColor(colorInt))
                        }
                )
            }
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .shadow(15.dp, CircleShape)
                    .clip(CircleShape)
                    .background(selectedCustomColor ?: MaterialTheme.colorScheme.surface)
                    .border(
                        width = 3.dp,
                        color = if (viewModel.noteColor.value == selectedCustomColor?.toArgb()) {
                            MaterialTheme.colorScheme.primary
                        } else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable {
                        viewModel.toggleDialogVisibility()
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.FormatPaint,
                    contentDescription = stringResource(R.string.select_color),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TransparentHintTextField(
            text = titleState.text,
            hint = titleState.hint?.let { stringResource(it) } ?: "",
            onValueChange = {
                viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it))
            },
            onFocusChange = {
                viewModel.onEvent(AddEditNoteEvent.ChangeTitleFocus(it))
            },
            isHintVisible = titleState.isHintVisible,
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        TransparentHintTextField(
            text = contentState.text,
            hint = contentState.hint?.let { stringResource(it) } ?: "",
            onValueChange = {
                viewModel.onEvent(AddEditNoteEvent.EnteredContent(it))
            },
            onFocusChange = {
                viewModel.onEvent(AddEditNoteEvent.ChangeContentFocus(it))
            },
            isHintVisible = contentState.isHintVisible,
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
fun ColorPickerDialog(
    onColorChanged: (HsvColor) -> Unit,
    onDismissRequest: () -> Unit,
    color: HsvColor
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
    ) {
        Column(
            modifier = Modifier
                .size(300.dp, 400.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.select_color),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            ClassicColorPicker(
                modifier = Modifier.size(300.dp),
                color = color,
                onColorChanged = onColorChanged
            )
        }
    }
}