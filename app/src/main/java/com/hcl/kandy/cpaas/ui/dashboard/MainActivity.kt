package com.hcl.kandy.cpaas.ui.login

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.databinding.ActivityMainBinding
import com.hcl.kandy.cpaas.ui.base.BaseActivity
import com.hcl.kandy.cpaas.ui.dashboard.HomeFragment
import com.hcl.kandy.cpaas.ui.dashboard.call.StartCallFragment
import com.hcl.kandy.cpaas.utils.ProgressUtils
import com.hcl.kandy.cpaas.utils.Utils
import com.rbbn.cpaas.mobile.CPaaS
import com.rbbn.cpaas.mobile.authentication.api.Authentication
import com.rbbn.cpaas.mobile.authentication.api.ConnectionCallback
import com.rbbn.cpaas.mobile.call.api.*
import com.rbbn.cpaas.mobile.utilities.Configuration
import com.rbbn.cpaas.mobile.utilities.Globals
import com.rbbn.cpaas.mobile.utilities.exception.MobileError
import com.rbbn.cpaas.mobile.utilities.exception.MobileException
import com.rbbn.cpaas.mobile.utilities.logging.LogLevel
import com.rbbn.cpaas.mobile.utilities.services.ServiceInfo
import com.rbbn.cpaas.mobile.utilities.services.ServiceType
import com.rbbn.cpaas.mobile.utilities.webrtc.CodecSet
import com.rbbn.cpaas.mobile.utilities.webrtc.ICEOptions
import com.rbbn.cpaas.mobile.utilities.webrtc.ICEServers

class MainActivity : BaseActivity() {
    private lateinit var callService: CallService
    private lateinit var binding: ActivityMainBinding
    lateinit var backparams: Bundle
    private lateinit var mCpaas: CPaaS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        progressUtils = ProgressUtils(this)
        activity = this

        backparams = intent.getBundleExtra("android.intent.extra.INTENT")!!
        Utils.instance.dumpIntent(backparams)

        initSDK()

    }

    private fun initSDK() {
        progressUtils.showDialog()
        Configuration.getInstance().restServerUrl = backparams[Utils.instance.base_url].toString()
        Configuration.getInstance().logLevel = LogLevel.TRACE
        val configuration = Configuration.getInstance()
        configuration.iceOption = ICEOptions.ICE_VANILLA
        configuration.iceCollectionTimeout = 12
        val iceServers = ICEServers()
        iceServers.addICEServer("turns:turn-ucc-1.genband.com:443?transport=tcp")
        iceServers.addICEServer("turns:turn-ucc-2.genband.com:443?transport=tcp")
        iceServers.addICEServer("stun:turn-ucc-1.genband.com:3478?transport=udp")
        iceServers.addICEServer("stun:turn-ucc-2.genband.com:3478?transport=udp")
        configuration.iceServers = iceServers
        Globals.setApplicationContext(activity)
        val lifetime = 3600 //in seconds
        val services: MutableList<ServiceInfo> = ArrayList()
        services.add(ServiceInfo(ServiceType.SMS, true))
        services.add(ServiceInfo(ServiceType.CALL, true))
        services.add(ServiceInfo(ServiceType.CHAT, true))
        services.add(ServiceInfo(ServiceType.ADDRESSBOOK, true))
        mCpaas = CPaaS(services)
        val authentication: Authentication = mCpaas.getAuthentication()
        authentication.setToken(backparams[Utils.instance.access_token].toString())
        try {
            authentication.connect(
                backparams[Utils.instance.id_token].toString(),
                lifetime,
                object : ConnectionCallback {
                    override fun onSuccess(channelInfo: String) {
                        progressUtils.dismissDialog()
                        Log.d("CpaaSSubscribe", channelInfo)
                        var f: Fragment = HomeFragment()
                        loadFragment(f)

                        callService = mCpaas.getCallService()
                        callService.setCallApplicationListener(object : CallApplicationListener {
                            override fun incomingCall(incomingCallInterface: IncomingCallInterface) {
                                Utils.debug("incomingCall");
                                val fragment = supportFragmentManager
                                    .findFragmentByTag(mFragmentStack!!.peek())
                                if (fragment is StartCallFragment) {
                                } else {
                                    AlertDialog.Builder(activity)
                                        .setTitle(activity.getString(R.string.app_name))
                                        .setCancelable(false)
                                        .setMessage("Received call")
                                        .setPositiveButton("Accept") { dialog, which ->
                                            currentCallInterface = incomingCallInterface
                                            val bundle = Bundle()
                                            bundle.putBoolean("isVideoChecked", false)
                                            bundle.putBoolean("isdoubleMLineChecked", false)
                                            bundle.putBoolean("placeCall", false)
                                            bundle.putString(
                                                "DestinationAddress",
                                                incomingCallInterface.callerName
                                            )

                                            val f: Fragment = StartCallFragment()
                                            f.arguments = bundle
                                            addFragment(f)
                                        }.setNegativeButton("Reject") { dialog, which ->
                                            incomingCallInterface.rejectCall();
                                        }
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show()
                                }
                            }

                            override fun callStatusChanged(
                                callInterface: CallInterface,
                                callState: CallState
                            ) {
                                Utils.debug("callStatusChanged");
                                val fragment = supportFragmentManager
                                    .findFragmentByTag(mFragmentStack!!.peek())
                                if (fragment is StartCallFragment) {
                                    (fragment as StartCallFragment).changecallstate(callState)
                                }
                            }

                            override fun mediaAttributesChanged(
                                callInterface: CallInterface,
                                mediaAttributes: MediaAttributes
                            ) {
                                Utils.debug("mediaAttributesChanged");
                            }

                            override fun callAdditionalInfoChanged(
                                callInterface: CallInterface,
                                map: Map<String, String>
                            ) {
                                currentCallInterface = callInterface
                                Utils.debug("callAdditionalInfoChanged");
                            }

                            override fun errorReceived(
                                callInterface: CallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("errorReceived");
                            }

                            override fun errorReceived(mobileError: MobileError) {
                                Utils.debug("errorReceived");
                            }

                            override fun establishCallSucceeded(outgoingCallInterface: OutgoingCallInterface) {
                                Utils.debug("establishCallSucceeded");
                            }

                            override fun establishCallFailed(
                                outgoingCallInterface: OutgoingCallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("establishCallFailed");
                            }

                            override fun acceptCallSucceed(incomingCallInterface: IncomingCallInterface) {
                                Utils.debug("acceptCallSucceed");
                            }

                            override fun acceptCallFailed(
                                incomingCallInterface: IncomingCallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("acceptCallFailed");
                            }

                            override fun rejectCallSucceeded(incomingCallInterface: IncomingCallInterface) {
                                Utils.debug("rejectCallSucceeded");
                            }

                            override fun rejectCallFailed(
                                incomingCallInterface: IncomingCallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("rejectCallFailed");
                            }

                            override fun ignoreSucceed(incomingCallInterface: IncomingCallInterface) {
                                Utils.debug("ignoreSucceed");
                            }

                            override fun ignoreFailed(
                                incomingCallInterface: IncomingCallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("ignoreFailed");
                            }

                            override fun forwardCallSucceeded(incomingCallInterface: IncomingCallInterface) {
                                Utils.debug("forwardCallSucceeded");
                            }

                            override fun forwardCallFailed(
                                incomingCallInterface: IncomingCallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("forwardCallFailed");
                            }

                            override fun videoStopSucceed(callInterface: CallInterface) {
                                Utils.debug("videoStopSucceed");
                            }

                            override fun videoStopFailed(
                                callInterface: CallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("videoStopFailed");
                            }

                            override fun videoStartSucceed(callInterface: CallInterface) {
                                Utils.debug("videoStartSucceed");
                            }

                            override fun videoStartFailed(
                                callInterface: CallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("videoStartFailed");
                            }

                            override fun muteCallSucceed(callInterface: CallInterface) {
                                Utils.debug("muteCallSucceed");
                            }

                            override fun muteCallFailed(
                                callInterface: CallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("muteCallFailed");
                            }

                            override fun unMuteCallSucceed(callInterface: CallInterface) {
                                Utils.debug("unMuteCallSucceed");
                            }

                            override fun unMuteCallFailed(
                                callInterface: CallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("unMuteCallFailed");
                            }

                            override fun holdCallSucceed(callInterface: CallInterface) {
                                Utils.debug("holdCallSucceed");
                            }

                            override fun holdCallFailed(
                                callInterface: CallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("holdCallFailed");
                            }

                            override fun unHoldCallSucceed(callInterface: CallInterface) {
                                Utils.debug("unHoldCallSucceed");
                            }

                            override fun unHoldCallFailed(
                                callInterface: CallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("unHoldCallFailed");
                            }

                            override fun endCallSucceeded(callInterface: CallInterface) {
                                Utils.debug("endCallSucceeded");
                                val fragment = supportFragmentManager
                                    .findFragmentByTag(mFragmentStack!!.peek())
                                if (fragment is StartCallFragment) {
                                    onBackPressed()
                                }
                            }

                            override fun endCallFailed(
                                callInterface: CallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("endCallFailed");
                            }

                            override fun ringingFeedbackSucceeded(incomingCallInterface: IncomingCallInterface) {
                                Utils.debug("ringingFeedbackSucceeded");
                            }

                            override fun ringingFeedbackFailed(
                                incomingCallInterface: IncomingCallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("ringingFeedbackFailed");
                            }

                            override fun transferCallSucceed(callInterface: CallInterface) {
                                Utils.debug("transferCallSucceed");
                            }

                            override fun transferCallFailed(
                                callInterface: CallInterface,
                                mobileError: MobileError
                            ) {
                                Utils.debug("transferCallFailed");
                            }

                            override fun notifyCallProgressChange(callInterface: CallInterface) {
                                Utils.debug("notifyCallProgressChange");
                            }
                        })
                    }

                    override fun onFail(mobileError: MobileError) {
                        progressUtils.dismissDialog()

                        if (mobileError != null) Log.d(
                            "CpaaSSubscribe",
                            mobileError.getErrorMessage()
                        ) else Log.d("CpaasSubscribe", "error")
                    }
                })
        } catch (e: MobileException) {
            e.printStackTrace()
        }
    }


    fun getCpaas(): CPaaS {
        return mCpaas
    }

    fun endCall() {
        if (currentCallInterface != null)
            currentCallInterface.endCall()
    }

    lateinit var currentCallInterface: CallInterface
}
