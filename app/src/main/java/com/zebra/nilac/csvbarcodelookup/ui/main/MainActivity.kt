package com.zebra.nilac.csvbarcodelookup.ui.main

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.zebra.led.ILed
import com.zebra.led.ILed.LED_LEFT
import com.zebra.led.ILed.LED_RIGHT
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.databinding.ActivityMainBinding
import com.zebra.nilac.csvbarcodelookup.models.Product


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()
    private var mLedService: ILed? = null

    private var errorSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.productResponse.observe(this, productObserver)

        val intent =
            Intent().setComponent(ComponentName("com.zebra.led", "com.zebra.led.LedService"))
        bindService(intent, ledConnection, BIND_AUTO_CREATE)
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(ledConnection)
    }

    private val scannerReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Received a DataWedge scanner intent: $intent")

            val barcode = intent.getStringExtra(AppConstants.DW_DATA_STRING_TAG)!!
            mainViewModel.getProductByBarcode(barcode)
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

    private fun updateLedStatus(isBadScan: Boolean) {
        mLedService?.setLed(LED_RIGHT, if (isBadScan) Color.RED else Color.GREEN)
        mLedService?.setLed(LED_LEFT, if (isBadScan) Color.RED else Color.GREEN)

        Handler(Looper.myLooper()!!).postDelayed({
            mLedService?.setLed(LED_RIGHT, Color.TRANSPARENT)
            mLedService?.setLed(LED_LEFT, Color.TRANSPARENT)
        }, 500L)
    }

    private fun showErrorSnackBar() {
        errorSnackbar =
            Snackbar.make(
                binding.root,
                getString(R.string.main_screen_barcode_scan_failed),
                Snackbar.LENGTH_INDEFINITE
            ).also {
                it.setBackgroundTint(getColor(R.color.secondary_color))
                it.setTextColor(getColor(R.color.black))
                it.show()
            }
    }

    private val productObserver: Observer<Product?> =
        Observer { product ->
            binding.genericStartMessage.visibility = View.GONE
            if (product == null) {
                showErrorSnackBar()
            } else {
                binding.productNumber.text = product.number.toString()
                binding.productName.text = product.name
                binding.productDescription.text = product.description

                errorSnackbar?.dismiss()
            }
            updateLedStatus(product == null)
        }

    companion object {
        const val TAG = "MainActivity"
    }
}