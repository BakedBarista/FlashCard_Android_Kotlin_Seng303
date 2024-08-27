package nz.ac.canterbury.seng303.lab2.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.FlowPreview
import nz.ac.canterbury.seng303.lab2.models.Flashcard
import nz.ac.canterbury.seng303.lab2.models.Note
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashcardViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "nz.ac.canterbury.seng303.lab1.shared.preferences")

@FlowPreview
val dataAccessModule = module {
    single<Storage<Flashcard>> {
        PersistentStorage(
            gson = get(),
            type = object: TypeToken<List<Flashcard>>(){}.type,
            preferenceKey = stringPreferencesKey("flashcards"),
            dataStore = androidContext().datastore
        )
    }

    single { Gson() }

    viewModel {
        FlashcardViewModel(
            flashcardStorage = get(),
            context = androidContext()
        )
    }
}