package com.internalexam.ui.submissions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.internalexam.databinding.FragmentSubmissionDetailBinding
import com.internalexam.model.SubmissionDetailHeader
import com.internalexam.model.SubmissionQuestionItem
import com.internalexam.ui.exam.UiState
import kotlinx.coroutines.launch

class SubmissionDetailFragment : Fragment() {

    private var _binding: FragmentSubmissionDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubmissionDetailViewModel by viewModels()
    private lateinit var questionAdapter: QuestionAdapter

    private var submissionId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            submissionId = it.getLong("submissionId", -1L)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubmissionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupTabLayout()
        setupBottomActions()
        setupListeners()
        observeViewModel()

        val idToLoad = if (submissionId != -1L) submissionId else 1001L
        viewModel.loadSubmissionDetail(idToLoad)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        questionAdapter = QuestionAdapter()
        binding.rvQuestions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = questionAdapter
        }
    }

    private fun setupTabLayout() {
        binding.tabLayoutFilter.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.filterByTab(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupBottomActions() {
        binding.btnComment.setOnClickListener {
            showCommentDialog()
        }

        binding.btnRegrade.setOnClickListener {
            showRegradeConfirmation()
        }

        binding.btnShare.setOnClickListener {
            shareSubmissionResult()
        }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            val idToLoad = if (submissionId != -1L) submissionId else 1001L
            viewModel.loadSubmissionDetail(idToLoad)
        }

        binding.btnRetry.setOnClickListener {
            val idToLoad = if (submissionId != -1L) submissionId else 1001L
            viewModel.loadSubmissionDetail(idToLoad)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.headerState.collect { header ->
                        header?.let { renderHeader(it) }
                    }
                }
                launch {
                    viewModel.uiState.collect { state ->
                        renderUiState(state)
                    }
                }
            }
        }
    }

    private fun renderHeader(header: SubmissionDetailHeader) {
        binding.tvStudentName.text = header.studentName
        binding.tvStudentId.text = "Mã HS: ${header.studentId}"
        binding.tvDetailScore.text = "%.1f / %.1f".format(header.score, header.totalScore)
        binding.tvSubmittedInfo.text = "Nộp lúc: ${header.submittedAt} • Thời gian làm: ${header.timeSpent} phút"
    }

    private fun renderUiState(state: UiState<List<SubmissionQuestionItem>>) {
        binding.swipeRefresh.isRefreshing = false

        when (state) {
            is UiState.Loading -> {
                binding.shimmerLayout.startShimmer()
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.swipeRefresh.visibility = View.GONE
                binding.tvEmpty.visibility = View.GONE
                binding.layoutError.visibility = View.GONE
            }
            is UiState.Success -> {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                binding.layoutError.visibility = View.GONE

                if (state.data.isEmpty()) {
                    binding.swipeRefresh.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.swipeRefresh.visibility = View.VISIBLE
                    questionAdapter.submitList(state.data)
                }
            }
            is UiState.Error -> {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                binding.swipeRefresh.visibility = View.GONE
                binding.tvEmpty.visibility = View.GONE
                binding.layoutError.visibility = View.VISIBLE
                binding.tvErrorMessage.text = state.message
            }
        }
    }

    private fun showCommentDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Nhập nhận xét của giáo viên..."
            setPadding(40, 30, 40, 30)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Nhận xét bài nộp")
            .setView(input)
            .setPositiveButton("Gửi") { dialog, _ ->
                val comment = input.text.toString()
                if (comment.isNotBlank()) {
                    Toast.makeText(requireContext(), "Đã lưu nhận xét!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Vui lòng nhập nội dung nhận xét", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showRegradeConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Chấm lại bài thi")
            .setMessage("Bạn có chắc chắn muốn yêu cầu hệ thống chấm lại bài nộp này?")
            .setPositiveButton("Chấm lại") { dialog, _ ->
                Toast.makeText(requireContext(), "Đang gửi yêu cầu chấm lại bài nộp...", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun shareSubmissionResult() {
        val header = viewModel.headerState.value
        val shareText = "Kết quả bài nộp của ${header?.studentName ?: "Học sinh"}: ${header?.score ?: "--"}/10 điểm."
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Chia sẻ kết quả bài nộp")
        startActivity(shareIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
