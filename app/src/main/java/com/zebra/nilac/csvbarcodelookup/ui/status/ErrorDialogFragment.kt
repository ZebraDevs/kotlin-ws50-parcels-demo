package com.zebra.nilac.csvbarcodelookup.ui.status

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.databinding.DialogErrorBinding
import java.util.*

class ErrorDialogFragment : DialogFragment() {

    private var mBinder: DialogErrorBinding? = null
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

        mBinder = DialogErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (requireArguments().containsKey(ERROR_MESSAGE)) {
            binding.loadingStatusText.text = arguments?.getString(ERROR_MESSAGE)
        }

        binding.mainContainer.setOnClickListener {
            dismiss()
        }

        Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    dismiss()
                    cancel()
                }
            }, 2000L)
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(true)
    }

    companion object {
        const val ERROR_MESSAGE = "com.zebra.nilac.csvbarcodelookup.ui.status.ERROR_MESSAGE"
    }
}