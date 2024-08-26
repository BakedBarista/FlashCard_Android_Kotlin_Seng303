package nz.ac.canterbury.seng303.lab2.screens

import android.content.Intent
import android.net.Uri
import android.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.lab2.models.Flashcard
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashcardViewModel

@Composable
fun FlashcardList(navController: NavController, flashcardViewModel: FlashcardViewModel) {
    flashcardViewModel.getFlashcards()
    val flashcards: List<Flashcard> by flashcardViewModel.flashcards.collectAsState(emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Add title at the top
        Text(
            text = "Flash Cards",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp) // Space between the title and the list
        )

        if (flashcards.isEmpty()) {
            EmptyStateMessage()
        } else {
            LazyColumn {
                items(flashcards) { flashcard ->
                    FlashcardItem(
                        flashcard = flashcard,
                        navController = navController,
                        deleteFn = { id: Int -> flashcardViewModel.deleteFlashcardById(id) }
                    )
                    Divider() // Add a divider between items
                }
            }
        }
    }
}

@Composable
fun FlashcardItem(
    flashcard: Flashcard,
    navController: NavController,
    deleteFn: (id: Int) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // Outer padding for spacing between cards
            .background(Color.LightGray, RoundedCornerShape(8.dp)) // Background color with rounded corners
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp)) // Border with rounded corners
            .clickable { navController.navigate("FlashcardDetail/${flashcard.id}") } // Make the entire box clickable
            .padding(16.dp) // Inner padding for content within the box
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = flashcard.question,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Icons row below the question text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButtonWithStyle(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(flashcard.question)}"))
                        context.startActivity(intent)
                    },
                    icon = Icons.Outlined.Search,
                    contentDescription = "Search"
                )
                IconButtonWithStyle(
                    onClick = {
                        navController.navigate("EditFlashcard/${flashcard.id}")
                    },
                    icon = Icons.Outlined.Edit,
                    contentDescription = "Edit"
                )
                IconButtonWithStyle(
                    onClick = {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Delete flashcard \"${flashcard.question}\"?")
                            .setCancelable(false)
                            .setPositiveButton("Delete") { dialog, id ->
                                deleteFn(flashcard.id)
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel") { dialog, id ->
                                dialog.dismiss()
                            }
                        val alert = builder.create()
                        alert.show()
                    },
                    icon = Icons.Outlined.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}




@Composable
fun EmptyStateMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "There are no cards created.\nPlease create some cards.",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Gray
        )
    }
}

@Composable
fun IconButtonWithStyle(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(120, 128, 191),
    contentColor: Color = Color.White
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .background(containerColor, RoundedCornerShape(8.dp))
            .border(1.dp, containerColor, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor
        )
    }
}
