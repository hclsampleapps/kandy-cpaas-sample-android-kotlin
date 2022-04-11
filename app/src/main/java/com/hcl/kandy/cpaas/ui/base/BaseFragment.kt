package com.hcl.kandy.cpaas.ui.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hcl.kandy.cpaas.ui.login.MainActivity

open class BaseFragment : Fragment(),
    View.OnClickListener {

    var mainView: View? = null
    lateinit var ctx: Context

    fun showToastS(message: String?) {
        val toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun onBackPressed() {
        (activity as MainActivity).onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = onCreateView(inflater, container, savedInstanceState)
        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ctx = activity?.applicationContext!!
    }

    fun addFragment(fragment: Fragment) {
        (activity as BaseActivity).addFragment(fragment)
    }

    protected open fun loadFragment(f: Fragment) {
        (activity as BaseActivity).loadFragment(f)
    }

    protected open fun goToActivity(activity: Activity, classActivity: Class<*>) {
        (activity as BaseActivity).goToActivity(activity, classActivity)
    }

    override fun onClick(p0: View?) {
    }


}
