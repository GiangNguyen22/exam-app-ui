package com.internalexam.ui.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.internalexam.databinding.FragmentExamListBinding
import com.internalexam.model.Exam
import kotlinx.coroutines.launch

class ExamListFragment : Fragment() {

    private var _binding: FragmentExamListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExamListViewModel by viewModels()
    private lateinit var examAdapter: ExamAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExamListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabLayout()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        examAdapter = ExamAdapter { exam ->
            onExamItemClicked(exam)
        }
        binding.rvExams.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = examAdapter
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val status = when (tab?.position) {
                    1 -> "ONGOING"
                    2 -> "UPCOMING"
                    3 -> "FINISHED"
                    else -> "ALL"
                }
                viewModel.filterByStatus(status)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getExams()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.getExams()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderUiState(state)
                }
            }
        }
    }

    private fun renderUiState(state: UiState<List<Exam>>) {
        binding.swipeRefresh.isRefreshing = false

        when (state) {
            is UiState.Loading -> {
                showLoadingState()
            }
            is UiState.Success -> {
                showSuccessState(state.data)
            }
            is UiState.Error -> {
                showErrorState(state.message)
            }
        }
    }

    private fun showLoadingState() {
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.swipeRefresh.visibility = View.GONE
        binding.tvEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    private fun showSuccessState(exams: List<Exam>) {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.layoutError.visibility = View.GONE

        if (exams.isEmpty()) {
            binding.swipeRefresh.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.swipeRefresh.visibility = View.VISIBLE
            examAdapter.submitList(exams)
        }
    }

    private fun showErrorState(message: String) {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.swipeRefresh.visibility = View.GONE
        binding.tvEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
    }

    private fun onExamItemClicked(exam: Exam) {
        val bundle = bundleOf(
            "examId" to exam.examId,
            "examTitle" to exam.title
        )
        try {
            // SafeArgs navigation with generated Direction or destination Bundle
            findNavController().navigate(
                // Example: ExamListFragmentDirections.actionExamListFragmentToExamDetailFragment(exam.examId, exam.title)
                // Using bundle navigation for compatibility:
                com.internalexam.R.id.toolbar, // fallback or safeArgs destination id if configured
                bundle
            )
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Chọn bài thi: ${exam.title} (ID: ${exam.examId})",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
