package com.zebra.nilac.csvbarcodelookup.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentDashboardBinding
import com.zebra.nilac.csvbarcodelookup.databinding.FragmentSettingsBinding

class SettingsActivity : Fragment() {

    private var mBinder: FragmentSettingsBinding? = null
    private val binding get() = mBinder!!

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    private var mAimTypePosition = 3

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

        mActivity = requireActivity() as MainActivity
        prepareUI()
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

        binding.continueButton.setOnClickListener {
            sendDWConfigurations()
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

//        val mainActivityIntent = Intent(mContext, MainActivity::class.java).apply {
//            putExtra(MainActivity.USE_LEDS, binding.useLedsChoiceYesRadio.isChecked)
//        }

        mActivity.goBackToDashboard()
    }
}