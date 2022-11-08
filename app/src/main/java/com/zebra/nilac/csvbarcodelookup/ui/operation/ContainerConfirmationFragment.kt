package com.zebra.nilac.csvbarcodelookup.ui.operation

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.zebra.nilac.csvbarcodelookup.*
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentContainerConfirmationBinding
import com.zebra.nilac.csvbarcodelookup.models.Event
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity
import com.zebra.nilac.csvbarcodelookup.utils.BeepControllerUtil
import com.zebra.nilac.csvbarcodelookup.utils.ContainerLocationsExtractor

class ContainerConfirmationFragment : Fragment() {

    private var mBinder: FragmentContainerConfirmationBinding? = null
    private val binding get() = mBinder!!

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    private lateinit var containerAnimation: AnimationDrawable
    private lateinit var mParcel: Parcel

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

        mBinder = FragmentContainerConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = requireActivity() as MainActivity
        mParcel = requireArguments().getParcelable<Parcel>("RetrievedParcel")!!

        internalViewModel.barcodeResponse.observe(viewLifecycleOwner, barcodeObserver)

        fillUI()
    }

    private fun fillUI() {
        binding.containerToScanTxtTitle.text = mParcel.assignedContainer

        //Load all the available container locations defined by the user
        val locations = ContainerLocationsExtractor.getLocations()

        for (location in locations) {
            val containerView =
                layoutInflater.inflate(R.layout.generic_location_container, null).apply {
                    findViewById<TextView>(R.id.location).apply {
                        text = location
                        if (mParcel.assignedContainer.contains(location)) {
                            setBackgroundResource(R.drawable.container_animation)
                            containerAnimation = background as AnimationDrawable
                        }
                    }
                }
            binding.containers.addView(containerView)
        }
        containerAnimation.start()
    }

    private fun goBackToParcelBarcodeScanScreen() {
        internalViewModel.barcodeResponse.removeObservers(viewLifecycleOwner)

        mActivity.goBackToParcelBarcodeScanScreen()
    }

    private val barcodeObserver: Observer<Event<String>> =
        Observer { barcodeEvent ->
            val barcode = barcodeEvent.contentIfNotHandled

            if (barcode.isNullOrEmpty()) {
                return@Observer
            }

            Log.d(TAG, "Received a DataWedge scanner intent: $barcode")

            if (barcode == mParcel.assignedContainer) {
                BeepControllerUtil(mContext).beep(true)

                mActivity.updateLedStatus(true)
                mActivity.showSuccessDialog()

                goBackToParcelBarcodeScanScreen()
            } else {
                BeepControllerUtil(mContext).beep(false)

                mActivity.updateLedStatus(false)
                mActivity.showErrorDialog(
                    "Wrong container!\n" +
                            "Please put parcel to ${mParcel.assignedContainer.substring(mParcel.assignedContainer.length - 2)} and confirm"
                )
            }
        }

    companion object {
        const val TAG = "ContainerConfirmationDialog"
    }
}