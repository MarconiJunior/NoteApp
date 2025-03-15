import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import com.marconi.noteapp.domain.model.InvalidNoteException
import com.marconi.noteapp.domain.model.Note
import com.marconi.noteapp.domain.use_case.NoteUseCases
import com.marconi.noteapp.events.CommonEvents
import com.marconi.noteapp.presentation.add_edit_note.AddEditNoteEvent
import com.marconi.noteapp.presentation.add_edit_note.AddEditNoteViewModel
import com.marconi.noteapp.rules.MainDispatcherRule
import com.marconi.noteapp.snackbar_utils.SnackbarController
import com.marconi.noteapp.snackbar_utils.SnackbarEvent
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.marconi.noteapp.R
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

@ExperimentalCoroutinesApi
class AddEditNoteViewModelTest {
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val noteUseCases: NoteUseCases = mockk(relaxed = true)
    private val commonEvents: CommonEvents = mockk(relaxed = true)
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)
    private val snackbarController: SnackbarController = mockk(relaxed = true)

    private lateinit var viewModel: AddEditNoteViewModel

    @Before
    fun setUp() {
        every { savedStateHandle.get<Int>("noteId") } returns null
        viewModel = AddEditNoteViewModel(noteUseCases, commonEvents, snackbarController, savedStateHandle)
    }

    @Test
    fun `assertUpdateStateOnPutTitle - should update note title when EnteredTitle event occurs`() {
        val newTitle = "Test Title"

        viewModel.onEvent(AddEditNoteEvent.EnteredTitle(newTitle))

        assertEquals(newTitle, viewModel.noteTitle.value.text)
    }

    @Test
    fun `savingNoteShouldCallAddNoteUseCaseAndHandleError - should call addNote and show error snackbar`() = runTest {
        val title = "Test Title"
        val content = "Test Content"
        val color = 123456
        val errorMessage = "The title of the note can't be empty"

        viewModel.onEvent(AddEditNoteEvent.EnteredTitle(title))
        viewModel.onEvent(AddEditNoteEvent.EnteredContent(content))
        viewModel.onEvent(AddEditNoteEvent.ChangeColor(color))

        coEvery { noteUseCases.addNote(any()) } throws InvalidNoteException(errorMessage)

        val snackbarSlot = slot<SnackbarEvent>()
        coEvery { snackbarController.sendEvent(capture(snackbarSlot)) } just Runs

        viewModel.onEvent(AddEditNoteEvent.SaveNote)
        advanceUntilIdle()

        coVerify { noteUseCases.addNote(any()) }
        coVerify { snackbarController.sendEvent(any()) }
        assertTrue(
            snackbarSlot.captured.message == errorMessage ||
                snackbarSlot.captured.message == R.string.couldn_t_save_note
        )
    }

    @Test
    fun `shouldRetrieveNoteWhenNoteIdIsPassed - should retrieve and populate note data when noteId is passed`() = runTest {
        val noteId = 1
        val note = Note(
            id = noteId,
            title = "Test Title",
            content = "Test Content",
            color = Color.Green.toArgb(),
            textColor = Color.Black.toArgb(),
            fontSize = 10f,
            timestamp = System.currentTimeMillis()
        )

        coEvery { noteUseCases.getNote(noteId) } returns note

        val savedStateHandleWithNoteId: SavedStateHandle = mockk()
        every { savedStateHandleWithNoteId.get<Int>("noteId") } returns noteId

        val viewModelWithNoteId = AddEditNoteViewModel(noteUseCases, commonEvents, snackbarController, savedStateHandleWithNoteId)

        delay(500)

        assertEquals(note.title, viewModelWithNoteId.noteTitle.value.text)
        assertEquals(note.content, viewModelWithNoteId.noteContent.value.text)
        assertEquals(note.color, viewModelWithNoteId.noteColor.value)
    }

    @Test
    fun `shouldUpdateNoteTitleWhenEnteredTitleEventOccurs - should update note title`() = runTest {
        val newTitle = "Updated Title"

        viewModel.onEvent(AddEditNoteEvent.EnteredTitle(newTitle))

        assertEquals(newTitle, viewModel.noteTitle.value.text)
    }

    @Test
    fun `shouldUpdateNoteContentWhenEnteredContentEventOccurs - should update note content`() = runTest {
        val newContent = "Updated Content"

        viewModel.onEvent(AddEditNoteEvent.EnteredContent(newContent))

        assertEquals(newContent, viewModel.noteContent.value.text)
    }

    @Test
    fun `shouldUpdateNoteColorWhenChangeColorEventOccurs - should update note color`() = runTest {
        val newColor = Color.Red.toArgb()

        viewModel.onEvent(AddEditNoteEvent.ChangeColor(newColor))

        assertEquals(newColor, viewModel.noteColor.value)
    }

    @Test
    fun `shouldHandleNonExistentNoteIdGracefully - should handle when noteId doesn't exist`() = runTest {
        val noteId = 999
        val savedStateHandleWithNoteId: SavedStateHandle = mockk()
        every { savedStateHandleWithNoteId.get<Int>("noteId") } returns noteId

        coEvery { noteUseCases.getNote(noteId) } returns null

        val viewModelWithNonExistentNoteId = AddEditNoteViewModel(noteUseCases, commonEvents, snackbarController, savedStateHandleWithNoteId)

        delay(500)

        assertEquals("", viewModelWithNonExistentNoteId.noteTitle.value.text)
        assertEquals("", viewModelWithNonExistentNoteId.noteContent.value.text)
    }

    @Test
    fun `savingNoteShouldShowSnackbarOnInvalidNote - should show snackbar if title or content is invalid`() = runTest {
        val errorMessage = "The title of the note can't be empty"
        val emptyTitle = ""

        viewModel.onEvent(AddEditNoteEvent.EnteredTitle(emptyTitle))
        viewModel.onEvent(AddEditNoteEvent.EnteredContent("Some content"))

        coEvery { noteUseCases.addNote(any()) } throws InvalidNoteException(errorMessage)

        val snackbarSlot = slot<SnackbarEvent>()
        coEvery { snackbarController.sendEvent(capture(snackbarSlot)) } just Runs

        viewModel.onEvent(AddEditNoteEvent.SaveNote)
        advanceUntilIdle()

        coVerify { noteUseCases.addNote(any()) }
        coVerify { snackbarController.sendEvent(any()) }
        assertTrue(snackbarSlot.captured.message == errorMessage)
    }
}