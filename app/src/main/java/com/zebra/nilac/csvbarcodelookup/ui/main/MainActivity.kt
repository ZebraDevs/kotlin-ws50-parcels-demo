package com.zebra.nilac.csvbarcodelookup.ui.main

import android.content.*
import android.graphics.Color
import android.os.*
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.zebra.led.ILed
import com.zebra.led.ILed.LED_LEFT
import com.zebra.led.ILed.LED_RIGHT
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.InternalViewModel
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.databinding.ActivityMainBinding
import com.zebra.nilac.csvbarcodelookup.models.Event
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import com.zebra.nilac.csvbarcodelookup.utils.BeepControllerUtil


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mNavigationController: NavController

    private val internalParcelViewModel: InternalViewModel by viewModels()
    private var mLedService: ILed? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(ledConnection)
    }

    private fun prepareUI() {
        //Check if we're using the app on a WS50 for Led Capabilities
        if (Build.MODEL == "WS50" && intent.getBooleanExtra(USE_LEDS, true)) {
            val intent =
                Intent().setComponent(ComponentName("com.zebra.led", "com.zebra.led.LedService"))
            bindService(intent, ledConnection, BIND_AUTO_CREATE)
        } else {
            //Register Scan Receiver
            registerReceiver(
                scannerReceiver,
                IntentFilter(AppConstants.DW_SCANNER_INTENT_ACTION)
            )
        }

        mNavigationController = findNavController(R.id.nav_host_fragment_content_main)

        //Start observing for parcel barcodes
        internalParcelViewModel.parcelResponse.observe(this, parcelObserver)
    }

    private fun goToContainerConfirmationScreen(parcel: Parcel) {
        mNavigationController.navigate(
            R.id.action_go_to_container_confirmation_fragment,
            Bundle().apply {
                putParcelable("RetrievedParcel", parcel)
            })
    }

    private val scannerReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Received a DataWedge scanner intent: $intent")

            val barcode = intent.getStringExtra(AppConstants.DW_DATA_STRING_TAG)!!

            if (mNavigationController.currentDestination?.id == R.id.parcel_barcode_scan_fragment) {
                internalParcelViewModel.getParcelByBarcode(barcode)
            } else {
                internalParcelViewModel.barcodeResponse.value = Event(barcode)
            }
        }
    }

    private val ledConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            mLedService = ILed.Stub.asInterface(iBinder)

            //Register Scan Receiver
            registerReceiver(
                scannerReceiver,
                IntentFilter(AppConstants.DW_SCANNER_INTENT_ACTION)
            )
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mLedService = null
        }
    }

    private val parcelObserver: Observer<Parcel?> =
        Observer { parcel ->
            if (parcel == null) {
                BeepControllerUtil(this).beep(false)
                updateLedStatus(false)
                showErrorDialog(getString(R.string.main_screen_barcode_scan_failed))
            } else {
                BeepControllerUtil(this).beep(true)
                showSuccessDialog()
                updateLedStatus(true)

                goToContainerConfirmationScreen(parcel)
            }
        }

    fun updateLedStatus(isGoodScan: Boolean) {
        mLedService?.setLed(LED_RIGHT, if (isGoodScan) Color.GREEN else Color.RED)
        mLedService?.setLed(LED_LEFT, if (isGoodScan) Color.GREEN else Color.RED)

        Handler(Looper.myLooper()!!).postDelayed({
            mLedService?.setLed(LED_RIGHT, Color.TRANSPARENT)
            mLedService?.setLed(LED_LEFT, Color.TRANSPARENT)
        }, 500L)
    }

    fun goToParcelBarcodeScanFragment() {
        mNavigationController.navigate(
            R.id.action_go_to_parcel_barcode_scan_fragment
        )
    }

    fun goToSettingsFragment() {
        mNavigationController.navigate(
            R.id.action_go_to_settings_fragment
        )
    }

    fun goToReportsSummaryFragment() {
        mNavigationController.navigate(
            R.id.action_go_to_reports_summary_fragment
        )
    }

    fun goBackToParcelBarcodeScanScreen() {
        mNavigationController.popBackStack()
    }

    fun goBackToDashboard() {
        mNavigationController.popBackStack()
    }

    companion object {
        const val TAG = "MainActivity"
        const val USE_LEDS = "com.zebra.nilac.csvbarcodelookup.ui.main.USE_LEDS"
    }
}