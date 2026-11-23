package com.internalexam.model

data class Exam(
    val examId: Long,
    val title: String,
    val subject: String,
    val className: String,
    val startTime: String,
    val duration: Int,
    val totalStudents: Int,
    val submittedCount: Int,
    val avgScore: Double?,
    val maxScore: Double?,
    val status: String
)
