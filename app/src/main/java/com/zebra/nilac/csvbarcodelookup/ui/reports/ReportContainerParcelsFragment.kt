package com.zebra.nilac.csvbarcodelookup.ui.reports

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentReportContainerParcelsBinding
import com.zebra.nilac.csvbarcodelookup.models.ReportContainer
import com.zebra.nilac.csvbarcodelookup.models.StoredParcel
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity
import com.zebra.nilac.csvbarcodelookup.ui.reports.adapters.ReportContainerParcelsListAdapter
import com.zebra.nilac.csvbarcodelookup.utils.KeyboardHelper

class ReportContainerParcelsFragment() : Fragment() {

    private var mBinder: FragmentReportContainerParcelsBinding? = null
    private val binding get() = mBinder!!

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    private var mParcels: ArrayList<StoredParcel> = ArrayList()

    private var reportContainerParcelsListAdapter: ReportContainerParcelsListAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinder = FragmentReportContainerParcelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = requireActivity() as MainActivity
        mParcels = requireArguments().getParcelableArrayList("StoredParcels")!!

        initSearchView()
        setUpAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        reportContainerParcelsListAdapter = null
    }

    private fun initSearchView() {
        binding.parcelsSearchview.apply {
            isFocusable = false
            isIconified = false
            clearFocus()
        }

        binding.parcelsSearchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.parcelsSearchview.clearFocus()

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredList = mParcels.filter {
                    it.parcelBarcode.contains(newText, ignoreCase = true)
                }.toMutableList()

                reportContainerParcelsListAdapter?.notifyAdapter(filteredList)

                return true
            }
        })
    }

    private fun setUpAdapter() {
        reportContainerParcelsListAdapter = ReportContainerParcelsListAdapter(mParcels, null)
        mBinder?.parcelsList!!.apply {
            layoutManager = LinearLayoutManager(mContext)
            itemAnimator = DefaultItemAnimator()
            adapter = reportContainerParcelsListAdapter
        }
    }
//
//    private val containerLocationObserver: Observer<List<ReportContainer>> = Observer {
//        if (it != null) {
//            containersListAdapter?.notifyAdapter(it)
//        }
//    }
}