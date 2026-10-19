package com.internalexam.data.repository

import com.internalexam.model.Submission

interface SubmissionRepository {
    suspend fun getSubmissionsByExamId(examId: Long): List<Submission>
}

class SubmissionRepositoryImpl : SubmissionRepository {
    override suspend fun getSubmissionsByExamId(examId: Long): List<Submission> {
        return listOf(
            Submission(
                submissionId = 1001L,
                studentId = "HS2024001",
                studentName = "Nguyễn Văn An",
                avatarUrl = null,
                submittedAt = "2025-03-20 08:42",
                score = 8.5,
                totalScore = 10.0,
                timeSpent = 35,
                correctCount = 17,
                wrongCount = 3,
                status = "SUBMITTED"
            ),
            Submission(
                submissionId = 1002L,
                studentId = "HS2024002",
                studentName = "Trần Thị Bình",
                avatarUrl = null,
                submittedAt = "2025-03-20 08:44",
                score = 9.2,
                totalScore = 10.0,
                timeSpent = 40,
                correctCount = 18,
                wrongCount = 2,
                status = "SUBMITTED"
            ),
            Submission(
                submissionId = 1003L,
                studentId = "HS2024003",
                studentName = "Lê Hoàng Cường",
                avatarUrl = null,
                submittedAt = "2025-03-20 08:48",
                score = 4.0,
                totalScore = 10.0,
                timeSpent = 45,
                correctCount = 8,
                wrongCount = 12,
                status = "LATE"
            ),
            Submission(
                submissionId = 1004L,
                studentId = "HS2024004",
                studentName = "Phạm Minh Dung",
                avatarUrl = null,
                submittedAt = "2025-03-20 08:30",
                score = 3.5,
                totalScore = 10.0,
                timeSpent = 20,
                correctCount = 7,
                wrongCount = 13,
                status = "VIOLATION"
            ),
            Submission(
                submissionId = 1005L,
                studentId = "HS2024005",
                studentName = "Đỗ Quang Hải",
                avatarUrl = null,
                submittedAt = "2025-03-20 08:40",
                score = 7.0,
                totalScore = 10.0,
                timeSpent = 38,
                correctCount = 14,
                wrongCount = 6,
                status = "SUBMITTED"
            ),
            Submission(
                submissionId = 1006L,
                studentId = "HS2024006",
                studentName = "Vũ Thu Hương",
                avatarUrl = null,
                submittedAt = "2025-03-20 08:45",
                score = 6.0,
                totalScore = 10.0,
                timeSpent = 42,
                correctCount = 12,
                wrongCount = 8,
                status = "SUBMITTED"
            )
        )
    }
}
