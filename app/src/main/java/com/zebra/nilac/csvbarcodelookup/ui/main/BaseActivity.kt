package com.zebra.nilac.csvbarcodelookup.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.ui.status.ErrorDialogFragment
import com.zebra.nilac.csvbarcodelookup.ui.status.SuccessDialogFragment

open class BaseActivity : AppCompatActivity() {

    private var mErrorDialog: ErrorDialogFragment? = null
    private var mSuccessDialog: SuccessDialogFragment? = null

    private val mSharedPreferences: SharedPreferences =
        DefaultApplication.getInstance().sharedPreferencesInstance!!

    override fun onDestroy() {
        super.onDestroy()

        mErrorDialog = null
        mSuccessDialog = null
    }

    fun showErrorDialog(message: String) {
        if (mErrorDialog != null && mErrorDialog!!.isVisible) {
            mErrorDialog!!.dismiss()
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        mErrorDialog = ErrorDialogFragment()

        val arguments = Bundle().apply {
            putString(ErrorDialogFragment.ERROR_MESSAGE, message)
        }
        mErrorDialog!!.arguments = arguments

        mErrorDialog?.isCancelable = false
        mErrorDialog?.show(transaction, ErrorDialogFragment::class.java.name)
    }

    fun showSuccessDialog(message: String) {
        if (mSharedPreferences.contains(AppConstants.USE_GREEN_DIALOG_WHILE_SCANNING) && !mSharedPreferences.getBoolean(
                AppConstants.USE_GREEN_DIALOG_WHILE_SCANNING,
                false
            )
        ) {
            return
        }

        if (mSuccessDialog != null && mSuccessDialog!!.isVisible) {
            mSuccessDialog!!.dismiss()
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        mSuccessDialog = SuccessDialogFragment()

        if (message.isNotEmpty()) {
            val arguments = Bundle().apply {
                putString(SuccessDialogFragment.MESSAGE, message)
            }
            mSuccessDialog!!.arguments = arguments
        }

        mSuccessDialog?.isCancelable = false
        mSuccessDialog?.show(transaction, SuccessDialogFragment::class.java.name)
    }
}