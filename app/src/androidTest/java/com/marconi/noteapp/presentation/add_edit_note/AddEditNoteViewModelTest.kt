package com.marconi.noteapp.presentation.add_edit_note

import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marconi.noteapp.domain.model.Note
import com.marconi.noteapp.presentation.MainActivity
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalTestApi
@RunWith(AndroidJUnit4::class)
class AddEditNoteViewModelTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val viewModel: AddEditNoteViewModel = mockk(relaxed = true)

    @Before
    fun setup() {
        composeTestRule.setContent {
            AddEditNoteScreen(navController = TestNavHostController(composeTestRule.activity), noteColor = Note.noteColors.first().toArgb())
        }
    }

    @Test
    fun testInitialScreenState() {
        composeTestRule.onNodeWithTag("TitleTextField").assertExists()
        composeTestRule.onNodeWithTag("ContentTextField").assertExists()
    }

    @Test
    fun testChangeNoteColor() {
        val colorBox = composeTestRule.onAllNodes(hasClickAction()).onFirst()
        colorBox.performClick()
        composeTestRule.waitForIdle()
        colorBox.assertIsSelected()
    }

    @Test
    fun testEnterTitleAndContent() {
        val titleNode = composeTestRule.onNodeWithTag("TitleTextField")
        val contentNode = composeTestRule.onNodeWithTag("ContentTextField")

        titleNode.performTextInput("Test Title")
        contentNode.performTextInput("Test Content")

        titleNode.assertTextEquals("Test Title")
        contentNode.assertTextEquals("Test Content")
    }
}