package com.internalexam.ui.submissions

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.internalexam.R
import com.internalexam.databinding.ItemSubmissionBinding
import com.internalexam.model.Submission

class SubmissionAdapter(
    private val onItemClick: (Submission) -> Unit
) : ListAdapter<Submission, SubmissionAdapter.SubmissionViewHolder>(SubmissionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmissionViewHolder {
        val binding = ItemSubmissionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubmissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubmissionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SubmissionViewHolder(
        private val binding: ItemSubmissionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(submission: Submission) {
            binding.apply {
                tvStudentName.text = submission.studentName
                tvStudentId.text = "Mã HS: ${submission.studentId}"
                tvSubmittedAt.text = "Nộp lúc: ${submission.submittedAt}"
                tvTimeSpent.text = "Thời gian làm: ${submission.timeSpent} phút"
                tvCorrectWrong.text = "${submission.correctCount} đúng / ${submission.wrongCount} sai"

                // Score formatting & color (< 5.0 is RED)
                tvScore.text = "%.1f".format(submission.score)
                if (submission.score < 5.0) {
                    tvScore.setTextColor(Color.parseColor("#FF6B6B"))
                } else {
                    tvScore.setTextColor(Color.parseColor("#35C2A5"))
                }

                // Status chip styling
                when (submission.status.uppercase()) {
                    "SUBMITTED" -> {
                        tvStatus.text = "Đã nộp"
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_mint)
                        tvStatus.setTextColor(Color.parseColor("#35C2A5"))
                    }
                    "LATE" -> {
                        tvStatus.text = "Nộp muộn"
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_amber)
                        tvStatus.setTextColor(Color.parseColor("#FFB74D"))
                    }
                    "VIOLATION" -> {
                        tvStatus.text = "Vi phạm"
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_blue)
                        tvStatus.setTextColor(Color.parseColor("#FF6B6B"))
                    }
                    else -> {
                        tvStatus.text = submission.status
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_indigo)
                        tvStatus.setTextColor(Color.parseColor("#6C5CE7"))
                    }
                }

                btnDetail.setOnClickListener {
                    onItemClick(submission)
                }

                root.setOnClickListener {
                    onItemClick(submission)
                }
            }
        }
    }

    private class SubmissionDiffCallback : DiffUtil.ItemCallback<Submission>() {
        override fun areItemsTheSame(oldItem: Submission, newItem: Submission): Boolean {
            return oldItem.submissionId == newItem.submissionId
        }

        override fun areContentsTheSame(oldItem: Submission, newItem: Submission): Boolean {
            return oldItem == newItem
        }
    }
}
