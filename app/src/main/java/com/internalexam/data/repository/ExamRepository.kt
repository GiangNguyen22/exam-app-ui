package com.internalexam.data.repository

import com.internalexam.model.Exam

interface ExamRepository {
    suspend fun getExams(): List<Exam>
}

class ExamRepositoryImpl : ExamRepository {
    override suspend fun getExams(): List<Exam> {
        return listOf(
            Exam(
                examId = 101L,
                title = "Kiểm tra Giữa kỳ I - Toán 12",
                subject = "Toán học",
                className = "12A1",
                startTime = "2025-03-20 08:00",
                duration = 45,
                totalStudents = 40,
                submittedCount = 38,
                avgScore = 7.8,
                maxScore = 10.0,
                status = "ONGOING"
            ),
            Exam(
                examId = 102L,
                title = "Thi thử THPT Quốc gia - Vật lý",
                subject = "Vật lý",
                className = "12A2",
                startTime = "2025-03-22 09:30",
                duration = 50,
                totalStudents = 38,
                submittedCount = 0,
                avgScore = null,
                maxScore = null,
                status = "UPCOMING"
            ),
            Exam(
                examId = 103L,
                title = "Kiểm tra 1 tiết - Hóa học",
                subject = "Hóa học",
                className = "12A1",
                startTime = "2025-03-10 14:00",
                duration = 45,
                totalStudents = 42,
                submittedCount = 42,
                avgScore = 8.2,
                maxScore = 9.8,
                status = "FINISHED"
            ),
            Exam(
                examId = 104L,
                title = "Kiểm tra Tiếng Anh Unit 5-8",
                subject = "Tiếng Anh",
                className = "12A3",
                startTime = "2025-03-05 10:15",
                duration = 60,
                totalStudents = 35,
                submittedCount = 35,
                avgScore = 6.9,
                maxScore = 9.5,
                status = "FINISHED"
            )
        )
    }
}
