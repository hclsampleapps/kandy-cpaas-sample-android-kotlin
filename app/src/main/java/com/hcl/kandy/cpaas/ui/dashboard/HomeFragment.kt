package com.hcl.kandy.cpaas.ui.dashboard

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.databinding.ActivityHomeBinding
import com.hcl.kandy.cpaas.ui.dashboard.addressbook.AddressBookList
import com.hcl.kandy.cpaas.ui.base.BaseFragment
import com.hcl.kandy.cpaas.ui.dashboard.call.CallFragment
import com.hcl.kandy.cpaas.ui.dashboard.chat.ChatFragment
import com.hcl.kandy.cpaas.ui.dashboard.multimedia.MultiMediaChatFragment
import com.hcl.kandy.cpaas.ui.dashboard.sms.SMSFragment
import com.hcl.kandy.cpaas.ui.login.LoginActivity
import com.hcl.kandy.cpaas.utils.MarshMallowPermission

class HomeFragment : BaseFragment() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_home, container, false)
        val marshMallowPermission = MarshMallowPermission(requireActivity())
        if (Build.VERSION.SDK_INT >= 23 && !marshMallowPermission.checkAllPermissions()) {
            marshMallowPermission.requestALLPermissions()
        }
        binding.chat.setOnClickListener(this)
        binding.sms.setOnClickListener(this)
        binding.call.setOnClickListener(this)
        binding.multiMedia.setOnClickListener(this)
        binding.addressbook.setOnClickListener(this)
        binding.logout.setOnClickListener(this)
        return binding.root
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.chat -> {
                Log.d("data", "<--------")
                addFragment(ChatFragment())
            }
            R.id.addressbook -> {
                Log.d("data", "<--------")
                addFragment(AddressBookList())
            }
            R.id.logout -> {
                goToActivity(requireActivity(), LoginActivity::class.java)
                requireActivity().finish()
            }
            R.id.call -> {
                Log.d("data", "<--------")
                addFragment(CallFragment())
            }
            R.id.sms -> {
                Log.d("data", "<--------")
                addFragment(SMSFragment())
            }
            R.id.multi_media -> {
                Log.d("data", "<--------")
                addFragment(MultiMediaChatFragment())
            }
        }
    }
}