package com.hcl.kandy.cpaas.ui.dashboard.multimedia

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.data.models.MultiMediaChatModel
import com.hcl.kandy.cpaas.databinding.FragmentMultimediaBinding
import com.hcl.kandy.cpaas.ui.base.BaseFragment
import com.hcl.kandy.cpaas.ui.login.MainActivity
import com.rbbn.cpaas.mobile.CPaaS
import com.rbbn.cpaas.mobile.messaging.api.*
import com.rbbn.cpaas.mobile.messaging.chat.api.*
import com.rbbn.cpaas.mobile.utilities.exception.MobileError

class MultiMediaChatFragment : BaseFragment() {
    private lateinit var binding: FragmentMultimediaBinding
    private lateinit var chatService: ChatService
    private lateinit var homeAdapter: MultiMediaChatAdapter
    private lateinit var mChatModels: ArrayList<MultiMediaChatModel>
    private var attachments: List<Attachment> = ArrayList()

    val ACTIVITY_CHOOSE_FILE = 1
    private var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_multimedia, container, false)
        mChatModels = ArrayList()

        val mainActivityContext: MainActivity = activity as MainActivity
        val cpaas: CPaaS = mainActivityContext.getCpaas()

        chatService = cpaas.getChatService()
        initChatService(context)

        homeAdapter = MultiMediaChatAdapter(mChatModels, chatService)
        binding.adapter = homeAdapter

        binding.btnFetchChat.setOnClickListener(this)
        binding.btnSendChat.setOnClickListener(this)
        binding.btnStartAttach.setOnClickListener(this)

        Log.d("data", "------------------>")
        return binding.root
    }

    private fun initChatService(context: Context?) {
        attachments = ArrayList()
        chatService.setChatListener(object : ChatListener {
            override fun inboundChatMessageReceived(inboundMessage: InboundMessage) {
                Log.d(
                    "CPaaS.ChatService",
                    "New message from " + inboundMessage.senderAddress + inboundMessage.message
                )
                val chatModel = MultiMediaChatModel(
                    inboundMessage.message,
                    inboundMessage.senderAddress,
                    true,
                    inboundMessage.messageId,
                    inboundMessage.parts
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

                val chatModel = MultiMediaChatModel(
                    outboundMessage.message,
                    outboundMessage.senderAddress,
                    false,
                    outboundMessage.messageId,
                    outboundMessage.parts
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

    }

    private fun notifyList(chatModel: MultiMediaChatModel) {
        mChatModels.add(chatModel)
        val mainHandler = Handler(requireContext().mainLooper)

        val myRunnable = Runnable {
            Log.d("cpaas", "notify list")
            homeAdapter.notifyDataSetChanged()
            binding.recycleView.scrollToPosition(mChatModels.size - 1)
        }
        mainHandler.post(myRunnable)

    }

    fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Choose a file"), ACTIVITY_CHOOSE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACTIVITY_CHOOSE_FILE) {
            if (data == null) return
            uri = data.data
            binding.imagePreview.setVisibility(View.VISIBLE)
            binding.imagePreview.setImageURI(uri)
        }
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

        if (uri != null) {
            if (uri != null) {
                val transferProgressListener =
                    TransferProgressListener { bytes, totalBytes ->
                        Log.d(
                            "CPass",
                            "Uploaded $bytes of $totalBytes bytes"
                        )
                    }


                val uploadCompleteListener: UploadCompleteListener =
                    object : UploadCompleteListener {
                        override fun uploadSuccess(attachment: Attachment) {
                            Log.i("", "Attachment uploaded")
                            attachments += attachment
                            for (attachment1 in attachments) {
                                message.attachFile(attachment1)
                            }
                            chatConversation.send(message, object : SendMessageCallback {
                                override fun onSuccess(outboundMessage: OutboundMessage?) {
                                    uri = null
                                    // hideProgressBAr()
                                    showToastS("Success")
                                    Log.d("CPaaS.ChatService", "Message is sent")
                                }

                                override fun onFail(error: MobileError) {
                                    Log.d("CPaaS.ChatService", "Message is failed")
                                    // hideProgressBAr()
                                    showToastS("Try again later")
                                }
                            })
                        }

                        override fun uploadFail(error: String) {
                            //hideProgressBAr()
                            showToastS(error);
                            // Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
                        }
                    }

                val handle = chatService.uploadAttachment(
                    uri,
                    transferProgressListener,
                    uploadCompleteListener
                )
            } else {

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
    }
}