package nz.ac.canterbury.seng303.lab2.models

data class Flashcard(
    val id: Int,
    val question: String,
    val answers: List<Answer>,
    val correctAnswerIndex: Int,
) : Identifiable {

    override fun getIdentifier(): Int {
        return id
    }
}
data class Answer(var text: String)

