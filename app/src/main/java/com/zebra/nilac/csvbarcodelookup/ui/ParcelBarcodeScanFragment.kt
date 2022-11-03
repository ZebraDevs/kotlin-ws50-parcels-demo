package com.zebra.nilac.csvbarcodelookup.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentContainerConfirmationBinding
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentParcelBarcodeScanBinding
import com.zebra.nilac.csvbarcodelookup.models.Parcel

class ParcelBarcodeScanFragment : Fragment() {

    private var mBinder: FragmentParcelBarcodeScanBinding? = null
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

        mBinder = FragmentParcelBarcodeScanBinding.inflate(inflater, container, false)
        return binding.root
    }
}