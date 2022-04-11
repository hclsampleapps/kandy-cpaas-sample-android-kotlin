package com.hcl.kandy.cpaas.utils

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

class Utils {

    companion object {
        fun debug(s: String) {
            System.out.println("" + s)
        }

        val instance = Utils()
    }

    val access_token = "access_token"
    val id_token = "id_token"
    val base_url = "base_url"
    val login_type = "login_type"

    fun dumpIntent(backparams: Bundle) {
        for (key in backparams.keySet()) {
            System.out.println("key=" + key + "=data=" + backparams[key])
        }
    }


    fun showToast(msg: String, activity: Activity?) {
        Toast.makeText(activity, "" + msg, Toast.LENGTH_LONG).show()
    }


    fun hideSoftKeyboard(activity: Activity?) {
        try {
            if (activity != null) {
                val inputMethodManager = activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                val v = activity.currentFocus
                if (v != null) {
                    val binder = activity.currentFocus!!.windowToken
                    if (binder != null && inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(binder, 0)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}