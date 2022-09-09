package com.zebra.nilac.csvbarcodelookup.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var mBinder: ActivitySettingsBinding

    private var mAimTypePosition = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinder = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mBinder.root)

        prepareUI()
    }

    private fun prepareUI() {
        val aimModesAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.aim_modes)
        )

        mBinder.inputAimType.setAdapter(aimModesAdapter)
        mBinder.inputAimType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mAimTypePosition = resources.getIntArray(R.array.aim_modes_t)[position]
            }

        mBinder.continueButton.setOnClickListener {
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
            if (mBinder.inputSameBarcodeTimeout.text.isNotEmpty()) {
                putString("same_barcode_timeout", mBinder.inputSameBarcodeTimeout.text.toString())
            }
            if (mBinder.inputAimTimer.text.isNotEmpty()) {
                putString("aim_timer", mBinder.inputAimTimer.text.toString())
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
        this.sendBroadcast(i)

        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.USE_LEDS, mBinder.useLedsChoiceYesRadio.isChecked)
        }

        finish()
        startActivity(mainActivityIntent)
    }
}