package nz.ac.canterbury.seng303.lab2.screens

import android.content.Intent
import android.net.Uri
import android.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun FlashcardItem(
    flashcard: Flashcard,
    navController: NavController,
    deleteFn: (id: Int) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { navController.navigate("FlashcardDetail/${flashcard.id}") },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display question
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
            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(flashcard.question)}"))
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = Color.Blue
                )
            }
            IconButton(onClick = {
                navController.navigate("EditFlashcard/${flashcard.id}")
            }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = Color.Blue
                )
            }
            IconButton(onClick = {
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
            }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
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
            text = "There are no flashcards created.\nPlease create some flashcards.",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Gray
        )
    }
}
