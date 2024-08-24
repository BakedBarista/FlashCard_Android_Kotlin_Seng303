package nz.ac.canterbury.seng303.lab2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nz.ac.canterbury.seng303.lab2.screens.CreateFlashcard
import nz.ac.canterbury.seng303.lab2.screens.EditFlashcard
import nz.ac.canterbury.seng303.lab2.screens.FlashcardList
import nz.ac.canterbury.seng303.lab2.screens.PlayFlashcardsScreen
import nz.ac.canterbury.seng303.lab2.ui.theme.Lab1Theme
import nz.ac.canterbury.seng303.lab2.viewmodels.CreateFlashcardViewModel
import nz.ac.canterbury.seng303.lab2.viewmodels.EditFlashcardViewModel
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashcardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel

class MainActivity : ComponentActivity() {

    private val flashcardViewModel: FlashcardViewModel by koinViewModel()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Lab1Theme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        // Add your AppBar content here
                        TopAppBar(
                            title = { Text("Flash Cards App") },
                            navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                ) {

                    Box(
                        modifier = Modifier
                            .padding(it) // Padding to account for the TopAppBar
                            .padding(start = 10.dp, end = 10.dp) // Gap from the screen edge
                            .fillMaxSize() // Fill available space
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 400.dp, height = 800.dp) // Specify width and height
                                .background(Color.Cyan, RoundedCornerShape(8.dp)) // Background with rounded corners
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp)) // Border with rounded corners
                                .padding(16.dp) // Inner padding
                                .align(Alignment.Center) // Center the inner Box within the parent
                        ) {
                            // Content inside the inner Box
                            val createFlashcardViewModel: CreateFlashcardViewModel = viewModel()
                            val editFlashcardViewModel: EditFlashcardViewModel = viewModel()
                            NavHost(navController = navController, startDestination = "Home") {
                                composable("Home") {
                                    Home(navController = navController)
                                }
                                composable("ViewFlashCards") {
                                    FlashcardList(navController, flashcardViewModel)
                                }
                                composable("PlayFlashcards") {
                                    PlayFlashcardsScreen(navController, flashcardViewModel)
                                }
                                composable("CreateFlashcard") {
                                    CreateFlashcard(
                                        navController = navController,
                                        question = createFlashcardViewModel.question,
                                        onQuestionChange = { newQuestion ->
                                                           createFlashcardViewModel.updateQuestion(
                                                               newQuestion
                                                           )
                                                           },
                                        answers = createFlashcardViewModel.answers.map { it },
                                        onAnswerChange = { index, answer ->
                                                         createFlashcardViewModel.updateAnswer(
                                                             index,
                                                             answer
                                                         )},
                                        correctAnswerIndex = createFlashcardViewModel.correctAnswerIndex,
                                        onCorrectAnswerChange = { newIndex ->
                                            createFlashcardViewModel.updateCorrectAnswerIndex(newIndex)
                                        },
                                        createFlashcardFn = { question, answers, correctAnswerIndex ->
                                            flashcardViewModel.createFlashcard(question, answers, correctAnswerIndex)
                                        },
                                        viewModel = createFlashcardViewModel
                                    )
                                }
                                composable("EditFlashcard/{flashcardId}") { backStackEntry ->
                                    val flashcardId = backStackEntry.arguments?.getString("flashcardId") ?: return@composable
                                    EditFlashcard(
                                        flashcardId = flashcardId,
                                        editFlashcardViewModel = editFlashcardViewModel,
                                        flashcardViewModel = flashcardViewModel,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Home(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("ViewFlashCards") }) {
            Text("View Flash Cards")
        }
        Button(onClick = { navController.navigate("CreateFlashcard") }) {
            Text("Create Flash Card")
        }
        Button(onClick = { navController.navigate("PlayFlashcards") }) {
            Text("Play Flash Cards")
        }
    }
}
