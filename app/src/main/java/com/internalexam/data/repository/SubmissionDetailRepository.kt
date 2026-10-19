package com.internalexam.data.repository

import com.internalexam.model.Option
import com.internalexam.model.Question
import com.internalexam.model.QuestionType
import com.internalexam.model.StudentAnswer
import com.internalexam.model.SubmissionDetailHeader
import com.internalexam.model.SubmissionQuestionItem

interface SubmissionDetailRepository {
    suspend fun getHeader(submissionId: Long): SubmissionDetailHeader
    suspend fun getQuestionItems(submissionId: Long): List<SubmissionQuestionItem>
}

class SubmissionDetailRepositoryImpl : SubmissionDetailRepository {

    override suspend fun getHeader(submissionId: Long): SubmissionDetailHeader {
        return SubmissionDetailHeader(
            submissionId = submissionId,
            studentName = "Nguyễn Văn An",
            studentId = "HS2024001",
            avatarUrl = null,
            score = 8.5,
            totalScore = 10.0,
            timeSpent = 35,
            submittedAt = "2025-03-20 08:42"
        )
    }

    override suspend fun getQuestionItems(submissionId: Long): List<SubmissionQuestionItem> {
        return listOf(
            SubmissionQuestionItem(
                question = Question(
                    questionId = 1,
                    content = "Câu 1: Hàm số y = x^3 - 3x có bao nhiêu điểm cực trị?",
                    type = QuestionType.SINGLE,
                    options = listOf(
                        Option("A", "0"),
                        Option("B", "1"),
                        Option("C", "2"),
                        Option("D", "3")
                    ),
                    correctAnswers = listOf("C"),
                    explanation = "y' = 3x^2 - 3 = 0 <=> x = +-1. Phương trình y' = 0 có 2 nghiệm phân biệt đổi dấu nên có 2 điểm cực trị."
                ),
                studentAnswer = StudentAnswer(
                    questionId = 1,
                    selectedAnswers = listOf("C"),
                    isCorrect = true,
                    score = 1.0
                )
            ),
            SubmissionQuestionItem(
                question = Question(
                    questionId = 2,
                    content = "Câu 2: Các mệnh đề nào sau đây đúng về hàm số mũ y = a^x (0 < a != 1)?",
                    type = QuestionType.MULTI,
                    options = listOf(
                        Option("A", "Tập xác định là R"),
                        Option("C", "Đồ thị luôn đi qua điểm (0, 1)"),
                        Option("B", "Tập giá trị là R"),
                        Option("D", "Hàm số luôn đồng biến trên R")
                    ),
                    correctAnswers = listOf("A", "C"),
                    explanation = "Tập xác định của y = a^x là R, đồ thị luôn qua (0, 1). Tập giá trị là (0; +inf)."
                ),
                studentAnswer = StudentAnswer(
                    questionId = 2,
                    selectedAnswers = listOf("A", "B"),
                    isCorrect = false,
                    score = 0.0
                )
            ),
            SubmissionQuestionItem(
                question = Question(
                    questionId = 3,
                    content = "Câu 3: Đồ thị hàm số y = (2x + 1) / (x - 1) có tiệm cận đứng x = 1.",
                    type = QuestionType.TRUE_FALSE,
                    options = listOf(
                        Option("TRUE", "Đúng"),
                        Option("FALSE", "Sai")
                    ),
                    correctAnswers = listOf("TRUE"),
                    explanation = "Mẫu số bằng 0 tại x = 1, lim(x->1) y = inf nên x = 1 là tiệm cận đứng."
                ),
                studentAnswer = StudentAnswer(
                    questionId = 3,
                    selectedAnswers = listOf("TRUE"),
                    isCorrect = true,
                    score = 1.0
                )
            ),
            SubmissionQuestionItem(
                question = Question(
                    questionId = 4,
                    content = "Câu 4 (Tự luận): Nêu ý nghĩa hình học của đạo hàm tại một điểm.",
                    type = QuestionType.ESSAY,
                    correctAnswers = emptyList(),
                    explanation = "Đạo hàm của hàm số y = f(x) tại điểm x0 là hệ số góc của tiếp tuyến của đồ thị hàm số tại điểm M0(x0, f(x0))."
                ),
                studentAnswer = StudentAnswer(
                    questionId = 4,
                    isCorrect = true,
                    score = 2.0,
                    essayText = "Đạo hàm tại điểm x0 bằng hệ số góc k của tiếp tuyến tại M(x0, y0)."
                )
            )
        )
    }
}
