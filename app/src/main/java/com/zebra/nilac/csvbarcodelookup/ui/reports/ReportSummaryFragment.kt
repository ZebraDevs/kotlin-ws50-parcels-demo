package com.zebra.nilac.csvbarcodelookup.ui.reports

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentReportContainersOverviewBinding
import com.zebra.nilac.csvbarcodelookup.models.ReportContainer
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity
import com.zebra.nilac.csvbarcodelookup.ui.reports.adapters.ReportsSummaryListAdapter

class ReportSummaryFragment() : Fragment() {

    private var mBinder: FragmentReportContainersOverviewBinding? = null
    private val binding get() = mBinder!!

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    private var containersListAdapter: ReportsSummaryListAdapter? = null

    private val reportsViewModel: ReportsViewModel by viewModels(
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

        reportsViewModel.containerResponse.observe(
            viewLifecycleOwner,
            containerLocationObserver
        )
        setUpAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reportsViewModel.containerResponse.removeObservers(viewLifecycleOwner)

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
        reportsViewModel.getReportContainersParcelsCount()
    }

    private val adapterCallBacks = object : ReportsSummaryListAdapter.CallBacks {
        override fun onSelectedContainer(container: ReportContainer) {
            mActivity.goToReportContainerParcelsFragment(reportsViewModel.getScannedParcelsByContainer(container.name))
        }
    }

    private val containerLocationObserver: Observer<List<ReportContainer>> = Observer {
        if (it != null) {
            containersListAdapter?.notifyAdapter(it)
        }
    }
}