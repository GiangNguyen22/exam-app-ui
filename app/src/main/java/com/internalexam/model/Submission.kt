package com.internalexam.model

data class Submission(
    val submissionId: Long,
    val studentId: String,
    val studentName: String,
    val avatarUrl: String?,
    val submittedAt: String,
    val score: Double,
    val totalScore: Double = 10.0,
    val timeSpent: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val status: String
)
