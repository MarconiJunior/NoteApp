package com.marconi.noteapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.marconi.noteapp.events.CommonEvents
import com.marconi.noteapp.data.data_source.NoteDao
import com.marconi.noteapp.data.data_source.NoteDatabase
import com.marconi.noteapp.data.repository.NoteRepositoryImpl
import com.marconi.noteapp.domain.repository.DeleteNote
import com.marconi.noteapp.domain.repository.NoteRepository
import com.marconi.noteapp.domain.use_case.AddNote
import com.marconi.noteapp.domain.use_case.GetNote
import com.marconi.noteapp.domain.use_case.GetNotes
import com.marconi.noteapp.domain.use_case.NoteUseCases
import com.marconi.noteapp.presentation.util.ThemeManager
import com.marconi.noteapp.snackbar_utils.SnackbarController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository)
        )
    }

    @Provides
    @Singleton
    fun provideCommonEvents(): CommonEvents = CommonEvents()

    @Provides
    @Singleton
    fun provideThemeManager(
        @ApplicationContext context: Context
    ) = ThemeManager(context)

    @Provides
    @Singleton
    fun provideSnackbarController() = SnackbarController()
}