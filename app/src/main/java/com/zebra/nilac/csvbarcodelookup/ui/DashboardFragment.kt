package com.zebra.nilac.csvbarcodelookup.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentDashboardBinding
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentParcelBarcodeScanBinding
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity

class DashboardFragment : Fragment() {

    private var mBinder: FragmentDashboardBinding? = null
    private val binding get() = mBinder!!

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinder = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = requireActivity() as MainActivity

        binding.parcelsScanEntry.setOnClickListener {
            mActivity.goToParcelBarcodeScanFragment()
        }

        binding.settingsEntry.setOnClickListener {
            mActivity.goToSettingsFragment()
        }
    }
}