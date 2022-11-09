package com.zebra.nilac.csvbarcodelookup.ui.reports

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zebra.nilac.csvbarcodelookup.InternalViewModel
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentReportContainersOverviewBinding
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentSettingsBinding
import com.zebra.nilac.csvbarcodelookup.models.ReportContainer
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity
import com.zebra.nilac.csvbarcodelookup.ui.reports.adapters.ReportsSummaryListAdapter
import com.zebra.nilac.csvbarcodelookup.utils.ContainerLocationsExtractor

class ReportsSummaryFragment() : Fragment() {

    private var mBinder: FragmentReportContainersOverviewBinding? = null
    private val binding get() = mBinder!!

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    private var containersListAdapter: ReportsSummaryListAdapter? = null

    private val reportsSummaryViewModel: ReportsSummaryViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinder = FragmentReportContainersOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = requireActivity() as MainActivity

        reportsSummaryViewModel.containerResponse.observe(
            viewLifecycleOwner,
            containerLocationObserver
        )
        setUpAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reportsSummaryViewModel.containerResponse.removeObservers(viewLifecycleOwner)

        containersListAdapter?.removeCallBacks()
        containersListAdapter = null
    }

    private fun setUpAdapter() {
        containersListAdapter = ReportsSummaryListAdapter(arrayListOf(), adapterCallBacks)
        mBinder?.containers!!.apply {
            layoutManager = LinearLayoutManager(mContext)
            itemAnimator = DefaultItemAnimator()
            adapter = containersListAdapter
        }
        reportsSummaryViewModel.getReportContainersParcelsCount()
    }

    private val adapterCallBacks = object : ReportsSummaryListAdapter.CallBacks {
        override fun onSelectedContainer(container: ReportContainer) {
        }
    }

    private val containerLocationObserver: Observer<List<ReportContainer>> = Observer {
        if (it != null) {
            containersListAdapter?.notifyAdapter(it)
        }
    }
}