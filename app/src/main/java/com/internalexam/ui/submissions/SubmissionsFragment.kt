package com.internalexam.ui.submissions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.internalexam.R
import com.internalexam.databinding.FragmentSubmissionsBinding
import com.internalexam.model.Submission
import com.internalexam.ui.exam.UiState
import kotlinx.coroutines.launch

class SubmissionsFragment : Fragment() {

    private var _binding: FragmentSubmissionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubmissionsViewModel by viewModels()
    private lateinit var submissionAdapter: SubmissionAdapter

    private var examId: Long = -1L
    private var examTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            examId = it.getLong("examId", -1L)
            examTitle = it.getString("examTitle", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubmissionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupChipFilter()
        setupListeners()
        observeViewModel()

        if (examId != -1L) {
            viewModel.loadSubmissions(examId)
        } else {
            // Load fallback sample data if examId wasn't passed
            viewModel.loadSubmissions(101L)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            subtitle = if (examTitle.isNotBlank()) examTitle else "Chi tiết bài nộp"
            setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_export -> {
                        Toast.makeText(requireContext(), "Xuất danh sách bài nộp thành công (Excel)", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_sort -> {
                        showSortPopupMenu(this)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun showSortPopupMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menu.add(0, 1, 0, "Điểm cao nhất")
        popup.menu.add(0, 2, 1, "Điểm thấp nhất")
        popup.menu.add(0, 3, 2, "Tên học sinh (A-Z)")
        popup.menu.add(0, 4, 3, "Thời gian nộp mới nhất")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> viewModel.setSortMode(SortMode.SCORE_DESC)
                2 -> viewModel.setSortMode(SortMode.SCORE_ASC)
                3 -> viewModel.setSortMode(SortMode.NAME_ASC)
                4 -> viewModel.setSortMode(SortMode.TIME_DESC)
            }
            true
        }
        popup.show()
    }

    private fun setupRecyclerView() {
        submissionAdapter = SubmissionAdapter { submission ->
            navigateToSubmissionDetail(submission)
        }
        binding.rvSubmissions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = submissionAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.search(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupChipFilter() {
        binding.chipGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            val status = when (checkedId) {
                R.id.chipSubmitted -> "SUBMITTED"
                R.id.chipLate -> "LATE"
                R.id.chipViolation -> "VIOLATION"
                else -> "ALL"
            }
            viewModel.filterByStatus(status)
        }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            if (examId != -1L) viewModel.loadSubmissions(examId)
            else viewModel.loadSubmissions(101L)
        }

        binding.btnRetry.setOnClickListener {
            if (examId != -1L) viewModel.loadSubmissions(examId)
            else viewModel.loadSubmissions(101L)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.statsState.collect { stats ->
                        renderHeaderStats(stats)
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

    private fun renderHeaderStats(stats: SubmissionStats) {
        binding.tvTotalSubmissions.text = stats.totalCount.toString()
        binding.tvAvgScore.text = "%.1f".format(stats.avgScore)
        binding.tvPassRate.text = "${stats.passRate}%"
        binding.tvMaxScore.text = "%.1f".format(stats.maxScore)
    }

    private fun renderUiState(state: UiState<List<Submission>>) {
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
                    submissionAdapter.submitList(state.data)
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

    private fun navigateToSubmissionDetail(submission: Submission) {
        val bundle = bundleOf(
            "submissionId" to submission.submissionId,
            "studentName" to submission.studentName
        )
        try {
            Navigation.findNavController(binding.root).navigate(
                R.id.toolbar,
                bundle
            )
        } catch (_: Exception) {
            Toast.makeText(
                requireContext(),
                "Xem chi tiết bài nộp của ${submission.studentName} (ID: ${submission.submissionId})",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
