package com.hcl.kandy.cpaas.ui.dashboard.sms

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.databinding.FragmentSmsBinding
import com.hcl.kandy.cpaas.ui.base.BaseFragment
import com.hcl.kandy.cpaas.ui.base.SMSModel
import com.hcl.kandy.cpaas.ui.login.MainActivity
import com.rbbn.cpaas.mobile.CPaaS
import com.rbbn.cpaas.mobile.messaging.api.InboundMessage
import com.rbbn.cpaas.mobile.messaging.api.MessageDeliveryStatus
import com.rbbn.cpaas.mobile.messaging.api.OutboundMessage
import com.rbbn.cpaas.mobile.messaging.api.SendMessageCallback
import com.rbbn.cpaas.mobile.messaging.sms.api.SMSConversation
import com.rbbn.cpaas.mobile.messaging.sms.api.SMSListener
import com.rbbn.cpaas.mobile.messaging.sms.api.SMSService
import com.rbbn.cpaas.mobile.utilities.exception.MobileError

class SMSFragment : BaseFragment() {
    private lateinit var binding: FragmentSmsBinding
    private lateinit var smsService: SMSService
    private lateinit var homeAdapter: SMSAdapter
    private lateinit var localAddressList: List<String>
    var select_position = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sms, container, false)
        mSMSModels = ArrayList()
        homeAdapter = SMSAdapter(mSMSModels)
        binding.adapter = homeAdapter

        val mainActivityContext: MainActivity = activity as MainActivity
        val cpaas: CPaaS = mainActivityContext.getCpaas()

        smsService = cpaas.getSMSService()
        localAddressList = smsService.localAddressList
        if (localAddressList != null && localAddressList.size > 0) {
            val builderSingle = AlertDialog.Builder(activity!!)
            builderSingle.setTitle("Select One Number:-")
            builderSingle.setCancelable(false)
            val arrayAdapter = ArrayAdapter<String>(
                activity!!,
                android.R.layout.select_dialog_singlechoice
            )

            for (i in localAddressList.indices) {
                arrayAdapter.add(localAddressList[i])
            }

            builderSingle.setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }

            builderSingle.setAdapter(
                arrayAdapter
            ) { dialog, position ->
                select_position = position
                val strName = arrayAdapter.getItem(select_position)
                binding.etSenderAddress.setText(strName)
                binding.etSenderAddress.setEnabled(false)
            }
            builderSingle.show()
        }
        smsService.setSMSListener(object : SMSListener {
            override fun inboundSMSMessageReceived(inboundMessage: InboundMessage) {
                Log.d(
                    "CPaaS.SMSService",
                    "New message from " + inboundMessage.senderAddress + inboundMessage.message
                )
                val smsModel = SMSModel(
                    inboundMessage.message,
                    inboundMessage.senderAddress,
                    true,
                    inboundMessage.messageId
                )
                notifyList(smsModel)
            }

            override fun SMSDeliveryStatusChanged(
                s: String,
                messageDeliveryStatus: MessageDeliveryStatus,
                s1: String
            ) {
            }

            override fun outboundSMSMessageSent(outboundMessage: OutboundMessage) {
                Log.d("CPaaS.SMSService", "Message is sent to " + outboundMessage.senderAddress)
                val smsModel = SMSModel(
                    outboundMessage.message,
                    outboundMessage.senderAddress,
                    false,
                    outboundMessage.messageId
                )
                notifyList(smsModel)
                activity!!.runOnUiThread { binding.etMessage.setText("") }
            }
        })
        binding.btnStartSMS.setOnClickListener(this)
        return binding.root
    }

    private lateinit var mSMSModels: java.util.ArrayList<SMSModel>

    private fun notifyList(smsModel: SMSModel) {
        mSMSModels.add(smsModel)
        val mainHandler = Handler(context!!.mainLooper)

        val myRunnable = Runnable {
            Log.d("cpaas", "notify list")
            homeAdapter.notifyDataSetChanged()
            binding.recycleView.scrollToPosition(mSMSModels.size - 1)
        }
        mainHandler.post(myRunnable)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnStartSMS -> {
                if (binding.etDestainationAddress.text.isEmpty()) {
                    showToastS("Enter Destination number")
                    return
                } else if (binding.etSenderAddress.text.isEmpty()) {
                    showToastS("Enter Sender number")
                    return
                } else if (binding.etMessage.text.isEmpty()) {
                    showToastS("Enter message")
                    return
                } else sendMessage()
            }
        }
    }

    private fun sendMessage() {
        val smsConversation = smsService.createConversation(
            binding.etDestainationAddress.text.toString(),
            localAddressList[select_position]
        ) as SMSConversation

        val message = smsService.createMessage(binding.etMessage.text.toString())

        smsConversation.send(message, object : SendMessageCallback {
            override fun onSuccess(outboundMessage: OutboundMessage) {
                Log.d("CPaaS.SMSService", "Message is sent")
                showToastS("Message is sent")
            }

            override fun onFail(error: MobileError) {
                Log.d("CPaaS.SMSService", "Message is failed")
                showToastS("Try again later")
            }
        })
    }
}