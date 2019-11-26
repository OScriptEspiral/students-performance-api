package model

import java.time.Instant

data class StudentPerformance(
    val _id: String?,
    val studentId: String,
    val cardId: String,
    val answer: String,
    val answeredCorrectly: Boolean,
    val studentEvaluationForTheCard: Byte,
    val answerDateTime: Long = Instant.now().toEpochMilli())