package com.internalexam.ui.exam

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.internalexam.R
import com.internalexam.databinding.ItemExamBinding
import com.internalexam.model.Exam

class ExamAdapter(
    private val onItemClick: (Exam) -> Unit
) : ListAdapter<Exam, ExamAdapter.ExamViewHolder>(ExamDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val binding = ItemExamBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExamViewHolder(
        private val binding: ItemExamBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(exam: Exam) {
            binding.apply {
                tvExamTitle.text = exam.title
                tvSubject.text = exam.subject
                tvClassName.text = "Lớp ${exam.className}"
                tvStartTime.text = exam.startTime
                tvDuration.text = "${exam.duration} phút"
                tvSubmittedCount.text = "${exam.submittedCount}/${exam.totalStudents} HS"

                tvAvgScore.text = exam.avgScore?.let { "$it điểm" } ?: "--"
                tvMaxScore.text = exam.maxScore?.let { "$it điểm" } ?: "--"

                when (exam.status) {
                    "ONGOING" -> {
                        tvStatus.text = "Đang diễn ra"
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_mint)
                        tvStatus.setTextColor(Color.parseColor("#35C2A5"))
                    }
                    "UPCOMING" -> {
                        tvStatus.text = "Sắp diễn ra"
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_blue)
                        tvStatus.setTextColor(Color.parseColor("#5B67F1"))
                    }
                    "FINISHED" -> {
                        tvStatus.text = "Đã kết thúc"
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_amber)
                        tvStatus.setTextColor(Color.parseColor("#FFB74D"))
                    }
                    else -> {
                        tvStatus.text = exam.status
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_indigo)
                        tvStatus.setTextColor(Color.parseColor("#6C5CE7"))
                    }
                }

                root.setOnClickListener {
                    onItemClick(exam)
                }
            }
        }
    }

    private class ExamDiffCallback : DiffUtil.ItemCallback<Exam>() {
        override fun areItemsTheSame(oldItem: Exam, newItem: Exam): Boolean {
            return oldItem.examId == newItem.examId
        }

        override fun areContentsTheSame(oldItem: Exam, newItem: Exam): Boolean {
            return oldItem == newItem
        }
    }
}
