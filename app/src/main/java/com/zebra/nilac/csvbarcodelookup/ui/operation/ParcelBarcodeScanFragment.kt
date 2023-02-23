package com.zebra.nilac.csvbarcodelookup.ui.operation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.InternalViewModel
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentParcelBarcodeScanBinding
import com.zebra.nilac.csvbarcodelookup.models.Event
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity

class ParcelBarcodeScanFragment : Fragment() {

    private var mBinder: FragmentParcelBarcodeScanBinding? = null
    private val binding get() = mBinder!!

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    private val mSharedPreferences: SharedPreferences =
        DefaultApplication.getInstance().sharedPreferencesInstance!!

    private val internalViewModel: InternalViewModel by viewModels(
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

        mBinder = FragmentParcelBarcodeScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = requireActivity() as MainActivity

        internalViewModel.parcelsAndStoredCount.observe(
            viewLifecycleOwner,
            object : Observer<Event<Array<Int>>> {
                override fun onChanged(countEvent: Event<Array<Int>>) {
                    val countArray = countEvent.contentIfNotHandled

                    if (countArray.isNullOrEmpty()) {
                        return
                    }

                    val storedCount = countArray[0]
                    val count = countArray[1]

                    mBinder?.parcelSuccessfulCount!!.text = getString(
                        R.string.main_screen_stored_parcels_count,
                        storedCount,
                        count
                    )

                    if (storedCount == count) {
                        if (mSharedPreferences.getBoolean(
                                AppConstants.RESET_REPORTS_AFTER_TASK_COMPLETED,
                                true
                            )
                        ) {
                            internalViewModel.resetReportsWithoutConfirmation()
                        }

                        mActivity.showSuccessDialog(getString(R.string.main_screen_task_completed_message))
                        mActivity.goBackToDashboard()
                    }
                }
            })
        internalViewModel.getParcelsAndStoredCount()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        internalViewModel.parcelsAndStoredCount.removeObservers(viewLifecycleOwner)
    }
}