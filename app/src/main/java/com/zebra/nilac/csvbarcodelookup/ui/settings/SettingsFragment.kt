package com.zebra.nilac.csvbarcodelookup.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentSettingsBinding
import com.zebra.nilac.csvbarcodelookup.models.DataImportObject
import com.zebra.nilac.csvbarcodelookup.ui.init.InitViewModel
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity

class SettingsFragment : Fragment() {

    private var mBinder: FragmentSettingsBinding? = null
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val binding get() = mBinder!!

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    private var mAimTypePosition = 1

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinder = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.isDataImported.observe(viewLifecycleOwner, dataImportObserver)
        settingsViewModel.areParcelLocationsImported.observe(
            viewLifecycleOwner,
            locationContainersObserver
        )
        settingsViewModel.isReportSessionReset.observe(
            viewLifecycleOwner,
            reportSessionResetObserver
        )

        mActivity = requireActivity() as MainActivity
        prepareUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        settingsViewModel.isDataImported.removeObservers(viewLifecycleOwner)
        settingsViewModel.isReportSessionReset.removeObservers(viewLifecycleOwner)
        settingsViewModel.areParcelLocationsImported.removeObservers(viewLifecycleOwner)
    }

    private fun prepareUI() {
        val aimModesAdapter: ArrayAdapter<String> = ArrayAdapter(
            mContext,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.aim_modes)
        )

        binding.inputAimType.setAdapter(aimModesAdapter)
        binding.inputAimType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mAimTypePosition = resources.getIntArray(R.array.aim_modes_t)[position]
            }

        binding.resetReportButton.setOnClickListener {
            settingsViewModel.resetReportSession()
        }

        binding.reimportParcelsData.setOnClickListener {
            settingsViewModel.importData()
        }

        binding.reimportContainersButton.setOnClickListener {
            settingsViewModel.importContainerLocations()
        }

        binding.continueButton.setOnClickListener {
            sendDWConfigurations()

            DefaultApplication.getInstance().sharedPreferencesInstance!!.edit().apply {
                putBoolean(
                    AppConstants.USE_GREEN_DIALOG_WHILE_SCANNING,
                    binding.useLedsChoiceYesRadio.isChecked
                )
            }.apply()

            mActivity.goBackToDashboard()
        }
    }

    private fun sendDWConfigurations() {
        val bMain = Bundle().apply {
            putString("PROFILE_NAME", "CsvBarcodeLookup")
            putString("PROFILE_ENABLED", "true")
            putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST")
        }

        val bParams = Bundle().apply {
            putString("aim_type", mAimTypePosition.toString())
            putString("scanner_selection_by_identifier", "INTERNAL_IMAGER");
            putString("scanner_input_enabled", "true");
            if (binding.inputSameBarcodeTimeout.text.isNotEmpty()) {
                putString("same_barcode_timeout", binding.inputSameBarcodeTimeout.text.toString())
            }
            if (binding.inputAimTimer.text.isNotEmpty()) {
                putString("aim_timer", binding.inputAimTimer.text.toString())
            }
        }

        val bConfig = Bundle().apply {
            putString("PLUGIN_NAME", "BARCODE")
            putString("RESET_CONFIG", "true")
            putBundle("PARAM_LIST", bParams)
        }

        bMain.putBundle("PLUGIN_CONFIG", bConfig)
        val i = Intent().apply {
            action = "com.symbol.datawedge.api.ACTION"
            putExtra("com.symbol.datawedge.api.SET_CONFIG", bMain)
        }
        mContext.sendBroadcast(i)
    }

    private val dataImportObserver: Observer<DataImportObject> =
        Observer { importObject ->
            if (importObject.isCSVDataImported) {
                Toast.makeText(
                    mContext,
                    "Parcels Data was successfully imported!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    mContext,
                    getString(
                        R.string.init_process_load_csv_data_failed,
                        importObject.errorMessage
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val locationContainersObserver: Observer<DataImportObject> =
        Observer { importObject ->
            if (importObject.isContainerLocationsImported) {
                Toast.makeText(
                    mContext,
                    "Container Locations were successfully imported!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    mContext,
                    getString(
                        R.string.init_process_load_csv_data_failed,
                        importObject.errorMessage
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val reportSessionResetObserver: Observer<Boolean> =
        Observer { isReset ->
            if (isReset) {
                Toast.makeText(
                    mContext,
                    "Current report session has been reset",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                //This should never happen...
                Toast.makeText(
                    mContext,
                    "Unable to reset current report session..",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
}