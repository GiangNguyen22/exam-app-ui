package com.internalexam.ui.submissions

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.internalexam.R
import com.internalexam.databinding.ItemQuestionBinding
import com.internalexam.model.QuestionType
import com.internalexam.model.SubmissionQuestionItem

class QuestionAdapter : ListAdapter<SubmissionQuestionItem, QuestionAdapter.QuestionViewHolder>(
    QuestionDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    inner class QuestionViewHolder(
        private val binding: ItemQuestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SubmissionQuestionItem, position: Int) {
            val question = item.question
            val answer = item.studentAnswer

            binding.apply {
                tvQuestionIndex.text = "Câu $position"
                tvQuestionContent.text = question.content
                tvQuestionScore.text = "%.1f điểm".format(answer.score)

                // Result badge styling
                if (answer.isCorrect) {
                    tvBadgeStatus.text = "Đúng"
                    tvBadgeStatus.setBackgroundResource(R.drawable.bg_chip_mint)
                    tvBadgeStatus.setTextColor(Color.parseColor("#35C2A5"))
                } else {
                    tvBadgeStatus.text = "Sai"
                    tvBadgeStatus.setBackgroundResource(R.drawable.bg_chip_blue)
                    tvBadgeStatus.setTextColor(Color.parseColor("#FF6B6B"))
                }

                // Render Options / Essay Container
                layoutOptionsContainer.removeAllViews()

                if (question.type == QuestionType.ESSAY) {
                    renderEssayAnswer(layoutOptionsContainer, answer.essayText)
                } else {
                    renderMultipleChoiceOptions(layoutOptionsContainer, question, answer)
                }

                // Explanation
                if (!question.explanation.isNullOrBlank() && (!answer.isCorrect || true)) {
                    layoutExplanation.visibility = View.VISIBLE
                    tvExplanation.text = question.explanation
                } else {
                    layoutExplanation.visibility = View.GONE
                }
            }
        }

        private fun renderEssayAnswer(container: LinearLayout, essayText: String?) {
            val context = container.context
            val textView = TextView(context).apply {
                text = "Bài làm tự luận: " + (essayText ?: "Chưa trả lời")
                setTextColor(Color.parseColor("#20213D"))
                textSize = 14f
                setPadding(32, 24, 32, 24)

                val bgDrawable = GradientDrawable().apply {
                    cornerRadius = 16f
                    setColor(Color.parseColor("#F6F4FF"))
                    setStroke(2, Color.parseColor("#E8E5F0"))
                }
                background = bgDrawable
            }
            container.addView(textView)
        }

        private fun renderMultipleChoiceOptions(
            container: LinearLayout,
            question: com.internalexam.model.Question,
            answer: com.internalexam.model.StudentAnswer
        ) {
            val context = container.context
            val selectedIds = answer.selectedAnswers.toSet()
            val correctIds = question.correctAnswers.toSet()

            question.options.forEach { option ->
                val isSelected = option.id in selectedIds
                val isCorrectAnswer = option.id in correctIds

                val optionView = TextView(context).apply {
                    text = "${option.id}. ${option.text}"
                    textSize = 14f
                    setPadding(36, 24, 36, 24)

                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 8, 0, 8)
                    }
                    this.layoutParams = layoutParams

                    val bgDrawable = GradientDrawable().apply {
                        cornerRadius = 16f
                    }

                    when {
                        // Option selected by student and IS CORRECT -> Solid Mint
                        isSelected && isCorrectAnswer -> {
                            bgDrawable.setColor(Color.parseColor("#1435C2A5"))
                            bgDrawable.setStroke(3, Color.parseColor("#35C2A5"))
                            setTextColor(Color.parseColor("#2BB5A0"))
                            setTypeface(null, Typeface.BOLD)
                            text = "${option.id}. ${option.text}  ✓ (Bạn chọn đúng)"
                        }
                        // Option selected by student but IS WRONG -> Red
                        isSelected && !isCorrectAnswer -> {
                            bgDrawable.setColor(Color.parseColor("#14FF6B6B"))
                            bgDrawable.setStroke(3, Color.parseColor("#FF6B6B"))
                            setTextColor(Color.parseColor("#FF6B6B"))
                            setTypeface(null, Typeface.BOLD)
                            text = "${option.id}. ${option.text}  ✗ (Bạn chọn)"
                        }
                        // Correct answer (not selected by student) -> Green border
                        isCorrectAnswer -> {
                            bgDrawable.setColor(Color.parseColor("#FFFFFF"))
                            bgDrawable.setStroke(3, Color.parseColor("#35C2A5"))
                            setTextColor(Color.parseColor("#35C2A5"))
                            setTypeface(null, Typeface.BOLD)
                            text = "${option.id}. ${option.text}  ✓ (Đáp án đúng)"
                        }
                        // Unselected option -> Default gray border
                        else -> {
                            bgDrawable.setColor(Color.parseColor("#FFFFFF"))
                            bgDrawable.setStroke(2, Color.parseColor("#E8E5F0"))
                            setTextColor(Color.parseColor("#20213D"))
                            setTypeface(null, Typeface.NORMAL)
                        }
                    }

                    background = bgDrawable
                }

                container.addView(optionView)
            }
        }
    }

    private class QuestionDiffCallback : DiffUtil.ItemCallback<SubmissionQuestionItem>() {
        override fun areItemsTheSame(
            oldItem: SubmissionQuestionItem,
            newItem: SubmissionQuestionItem
        ): Boolean {
            return oldItem.question.questionId == newItem.question.questionId
        }

        override fun areContentsTheSame(
            oldItem: SubmissionQuestionItem,
            newItem: SubmissionQuestionItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
