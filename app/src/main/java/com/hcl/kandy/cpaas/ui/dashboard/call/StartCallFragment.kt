package com.hcl.kandy.cpaas.ui.dashboard.call

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.databinding.FragmentCallStartBinding
import com.hcl.kandy.cpaas.ui.base.BaseFragment
import com.hcl.kandy.cpaas.ui.login.MainActivity
import com.hcl.kandy.cpaas.utils.Constant.Companion.TAG
import com.rbbn.cpaas.mobile.CPaaS
import com.rbbn.cpaas.mobile.call.api.*
import com.rbbn.cpaas.mobile.utilities.exception.MobileError

class StartCallFragment : BaseFragment() {
    private lateinit var binding: FragmentCallStartBinding
    private lateinit var callService: CallService
    private lateinit var incomingCall: IncomingCallInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_start, container, false)
        binding.activeCallHangupButton.setOnClickListener(this)
        val bundle = arguments
        binding.activeCallCallerName.text = bundle!!.getString("DestinationAddress")
        val mainActivityContext: MainActivity = activity as MainActivity
        val cpaas: CPaaS = mainActivityContext.getCpaas()

        callService = cpaas.getCallService()
        if (bundle!!.getBoolean("placeCall", false)) {
            callService.createOutgoingCall(
                bundle!!.getString("DestinationAddress", ""),
                object : OutgoingCallCreationCallback {
                    override fun callCreated(callInterface: OutgoingCallInterface?) {
                        if (bundle!!.getBoolean("isVideoChecked", false)) {
                            callInterface?.setRemoteVideoView(binding.remoteVideoView) // Provide a VideoView widget object to show the local video on the UI
                            callInterface?.setLocalVideoView(binding.localVideoView) // Provide a VideoView widget object to show the local video on the UI
                            callInterface?.establishCall(true)
                        } else {
                            callInterface?.establishAudioCall()
                        }
                    }

                    override fun callCreationFailed(error: MobileError?) {
                        Log.e(TAG, "callCreationFailed: " + error?.errorMessage)
                    }
                })
        } else {
            incomingCall = mainActivityContext.currentCallInterface as IncomingCallInterface
            if (incomingCall != null) {
                incomingCall.acceptCall(false)
            } else {
                onBackPressed()
            }
        }
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.activeCallHangupButton -> {
                (activity as MainActivity).endCall()
                onBackPressed()
            }
        }
    }

    fun changecallstate(callState: CallState) {
        binding.activeCallStateText.text = "" + callState.type.name
        if (callState.type.name.equals("ENDED")) {
            onBackPressed()
        }
    }
}