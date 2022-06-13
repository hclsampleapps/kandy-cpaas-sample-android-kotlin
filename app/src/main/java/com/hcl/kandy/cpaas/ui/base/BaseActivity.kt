package com.hcl.kandy.cpaas.ui.base

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.ui.dashboard.HomeFragment
import com.hcl.kandy.cpaas.utils.ProgressUtils
import com.hcl.kandy.cpaas.utils.Utils
import java.util.*

abstract class BaseActivity : AppCompatActivity(),
    View.OnClickListener {
    var mFragmentStack = Stack<String>()
    var mContent: Fragment? = null

    lateinit var activity: Activity
    private var backPressedToExitOnce: Boolean = false
    protected lateinit var progressUtils: ProgressUtils

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        window?.decorView?.setOnSystemUiVisibilityChangeListener {
            View.SYSTEM_UI_FLAG_FULLSCREEN;
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        activity = this;
        mFragmentStack = Stack()

    }
    fun showToastS(message: String?) {
        val toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    open fun hideAllFragment() {
        try {
            for (i in mFragmentStack!!.indices) {
                val transaction = supportFragmentManager
                    .beginTransaction()
                val fragment = supportFragmentManager
                    .findFragmentByTag(mFragmentStack!!.peek())
                if (fragment != null) transaction.hide(fragment)
                transaction.commit()
            }
            mFragmentStack!!.clear()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    open fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            hideAllFragment()
            addFragment(fragment)
        } else {
            Log.e("MainActivity", "Error in creating fragment.")
        }
    }

    open fun addFragment(fragment: Fragment) {
        try {
            mContent = fragment
            val transaction = supportFragmentManager
                .beginTransaction()
            if (mFragmentStack!!.size > 0) {
                val currentFragment = supportFragmentManager
                    .findFragmentByTag(mFragmentStack!!.peek())
                if (currentFragment != null) transaction.hide(currentFragment)
                mFragmentStack!!.add(mContent.toString())
                transaction.add(
                    R.id.content_frame, fragment,
                    mContent.toString()
                )
                transaction.addToBackStack(mContent.toString())
                transaction.commit()
            } else {
                mFragmentStack!!.add(mContent.toString())
                transaction.replace(
                    R.id.content_frame, fragment,
                    mContent.toString()
                )
                transaction.commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        Utils.instance.hideSoftKeyboard(activity)
        try {
            if (mFragmentStack?.size == 0) {
                finish()
            } else {
                val fragment = supportFragmentManager
                    .findFragmentByTag(mFragmentStack!!.peek())
                if (fragment is HomeFragment) {
                    if (backPressedToExitOnce) {
                        finish()
                    } else {
                        this.backPressedToExitOnce = true
                        Utils.instance.showToast("Press again to exit", activity);
                        Handler().postDelayed({ backPressedToExitOnce = false }, 2000)
                    }
                } else {
                    removeFragment()
                    super.onBackPressed()
                }
            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    open fun removeFragment() {
        try {
            val transaction = supportFragmentManager
                .beginTransaction()
            val fragment = supportFragmentManager.findFragmentByTag(
                mFragmentStack!!.peek()
            )
            mContent = fragment
            if (fragment != null) transaction.show(fragment)
            transaction.commit()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun goToActivity(activity: Activity, classActivity: Class<*>, bundle: Bundle) {
        Utils.instance.hideSoftKeyboard(activity)
        startActivity(
            Intent(activity, classActivity).putExtra("android.intent.extra.INTENT",
                bundle
            )
        )

    }

    fun goToActivity(activity: Activity, classActivity: Class<*>) {
        Utils.instance.hideSoftKeyboard(activity)
        startActivity(
            Intent(activity, classActivity)
        )
    }

    override fun onClick(v: View?) {

    }
}