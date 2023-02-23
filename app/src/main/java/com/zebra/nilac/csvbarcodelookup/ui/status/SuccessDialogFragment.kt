package com.zebra.nilac.csvbarcodelookup.ui.status

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.databinding.DialogSuccessBinding
import java.util.*

class SuccessDialogFragment : DialogFragment() {

    private var mBinder: DialogSuccessBinding? = null
    private val binding get() = mBinder!!

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_Base_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        mBinder = DialogSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = arguments
        if (arguments != null && arguments.containsKey(MESSAGE)) {
            binding.loadingStatusText.text = arguments.getString(MESSAGE)
        }

        Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    dismiss()
                    cancel()
                }
            }, 1600L)
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)
    }

    companion object {
        const val MESSAGE = "com.zebra.nilac.csvbarcodelookup.ui.status.MESSAGE"
    }
}