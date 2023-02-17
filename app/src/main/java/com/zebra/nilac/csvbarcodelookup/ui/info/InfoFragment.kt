package com.zebra.nilac.csvbarcodelookup.ui.info

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zebra.nilac.csvbarcodelookup.BuildConfig
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentInfoBinding
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentParcelBarcodeScanBinding

class InfoFragment : Fragment() {

    private var mBinder: FragmentInfoBinding? = null
    private val binding get() = mBinder!!

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinder = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinder?.appVersion!!.text = BuildConfig.VERSION_NAME
    }
}