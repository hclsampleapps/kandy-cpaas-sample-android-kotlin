package com.hcl.kandy.cpaas.ui.dashboard.chat

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.data.models.ChatModel
import com.hcl.kandy.cpaas.databinding.FragmentChatBinding
import com.hcl.kandy.cpaas.ui.base.BaseFragment
import com.hcl.kandy.cpaas.ui.login.MainActivity
import com.rbbn.cpaas.mobile.CPaaS
import com.rbbn.cpaas.mobile.messaging.api.*
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatConversation
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatGroupParticipant
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatListener
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatService
import com.rbbn.cpaas.mobile.utilities.exception.MobileError

class ChatFragment : BaseFragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var chatService: ChatService
    private lateinit var homeAdapter: ChatAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        mChatModels = ArrayList()
        homeAdapter = ChatAdapter(mChatModels)
        binding.adapter = homeAdapter

        val mainActivityContext: MainActivity = activity as MainActivity
        val cpaas: CPaaS = mainActivityContext.getCpaas()

        chatService = cpaas.getChatService()
        chatService.setChatListener(object : ChatListener {
            override fun inboundChatMessageReceived(inboundMessage: InboundMessage) {
                Log.d(
                    "CPaaS.ChatService",
                    "New message from " + inboundMessage.senderAddress + inboundMessage.message
                )
                val chatModel = ChatModel(
                    inboundMessage.message,
                    inboundMessage.senderAddress,
                    true,
                    inboundMessage.messageId
                )
                notifyList(chatModel)
            }

            override fun chatDeliveryStatusChanged(
                s: String,
                messageDeliveryStatus: MessageDeliveryStatus,
                s1: String
            ) {
            }

            override fun chatParticipantStatusChanged(
                chatGroupParticipant: ChatGroupParticipant,
                s: String
            ) {
            }

            override fun outboundChatMessageSent(outboundMessage: OutboundMessage) {
                Log.d("CPaaS.ChatService", "Message is sent to " + outboundMessage.senderAddress)
                val chatModel = ChatModel(
                    outboundMessage.message,
                    outboundMessage.senderAddress,
                    false,
                    outboundMessage.messageId
                )
                notifyList(chatModel)
                activity!!.runOnUiThread { binding.etMessage.setText("") }
            }

            override fun isComposingReceived(s: String, messageState: MessageState, l: Long) {}
            override fun groupChatSessionInvitation(
                list: List<ChatGroupParticipant>,
                s: String,
                s1: String
            ) {
            }

            override fun groupChatEventNotification(s: String, s1: String, s2: String) {}
        })

        binding.btnFetchChat.setOnClickListener(this)
        binding.btnStartChat.setOnClickListener(this)

        Log.d("data", "------------------>")
        return binding.root
    }

    private lateinit var mChatModels: java.util.ArrayList<ChatModel>
    private fun notifyList(chatModel: ChatModel) {
        mChatModels.add(chatModel)
        val mainHandler = Handler(context!!.mainLooper)

        val myRunnable = Runnable {
            Log.d("cpaas", "notify list")
            homeAdapter.notifyDataSetChanged()
            binding.recycleView.scrollToPosition(mChatModels.size - 1)
        }
        mainHandler.post(myRunnable)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFetchChat -> {
                if (binding.etDestainationAddress.text.isEmpty()) {
                    showToastS("Please enter Destination Address.")
                } else {
                    binding.etDestainationAddress.setEnabled(false)
                    binding.btnFetchChat.setVisibility(View.GONE)
                    binding.showChatLayout.setVisibility(View.VISIBLE)
                }
            }
            R.id.btnStartChat -> {
                if (binding.etMessage.text.isEmpty()) {
                    showToastS("Please enter Message.")

                } else {
                    sendMessage(
                        binding.etDestainationAddress.text.toString(),
                        binding.etMessage.text.toString()
                    )
                }
            }
        }
    }

    private fun sendMessage(participant: String, txt: String) {
        val chatConversation = chatService.createConversation(participant) as ChatConversation
        val message: OutboundMessage = chatService.createMessage(txt)
        chatConversation.send(message, object : SendMessageCallback {
            override fun onSuccess(outboundMessage: OutboundMessage) {
                Log.d("CPaaS.ChatService", "Message is sent")
            }

            override fun onFail(error: MobileError) {
                Log.d("CPaaS.ChatService", "Message is failed")
                showToastS("Try again later")
            }
        })
    }
}