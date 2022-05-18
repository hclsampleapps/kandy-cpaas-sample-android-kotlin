package com.hcl.kandy.cpaas.ui.dashboard.multimedia

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.data.models.MultiMediaChatModel
import com.hcl.kandy.cpaas.databinding.AdapterMultimediaChatBinding
import com.hcl.kandy.cpaas.databinding.AdapterMultimediaChatMeBinding
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatService
import com.rbbn.cpaas.mobile.messaging.chat.api.DownloadCompleteListener
import com.rbbn.cpaas.mobile.messaging.chat.api.TransferProgressListener
import com.rbbn.cpaas.mobile.messaging.chat.api.TransferRequestHandle
import java.io.ByteArrayOutputStream
import java.io.File


class MultiMediaChatAdapter(chatModels: ArrayList<MultiMediaChatModel>, chatService: ChatService) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private val DOWNLOAD_FOLDER = "/storage/emulated/0/Download"
    private val CHAT_TYPE_ME = 1
    private val CHAT_TYPE_OTHER = 2

    private val chatModels: ArrayList<MultiMediaChatModel> = chatModels
    private val chatService: ChatService = chatService

    override fun getItemViewType(position: Int): Int {
        val chatModel: MultiMediaChatModel = chatModels[position]
        return if (chatModel.isMessageIn) CHAT_TYPE_OTHER else CHAT_TYPE_ME
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == CHAT_TYPE_ME) {
            val binding = DataBindingUtil.inflate<AdapterMultimediaChatMeBinding>(
                LayoutInflater.from(viewGroup.context),
                R.layout.adapter_multimedia_chat_me, viewGroup, false
            )
            MyViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<AdapterMultimediaChatBinding>(
                LayoutInflater.from(viewGroup.context),
                R.layout.adapter_multimedia_chat, viewGroup, false
            )
            OtherViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        val chatModel: MultiMediaChatModel = chatModels[i]
        val destination: String = chatModel.destination
        when (holder.getItemViewType()) {
            CHAT_TYPE_ME -> {
                val myViewHolder = holder as MyViewHolder
                // myViewHolder.binding.txtDestination.text = destination
                myViewHolder.binding.txtMessage.text = (chatModel.messageTxt)

                if (chatModel.parts != null && chatModel.parts.size > 1) {
                    myViewHolder.binding.attachmentSent.setVisibility(View.VISIBLE);
                    downloadfile(
                        chatModel.parts.get(1).getFile().getLink(),
                        myViewHolder.binding.attachmentSent,
                        chatModel.parts.get(1).getFile().getName()
                    );
                } else {
                    myViewHolder.binding.attachmentSent.setVisibility(View.GONE);
                }

            }
            CHAT_TYPE_OTHER -> {
                val otherViewHolder = holder as OtherViewHolder

                if (chatModel.parts != null && chatModel.parts.size > 1) {
                    otherViewHolder.binding.attachmentSent.setVisibility(View.VISIBLE);
                    downloadfile(
                        chatModel.parts.get(1).getFile().getLink(),
                        otherViewHolder.binding.attachmentSent,
                        chatModel.parts.get(1).getFile().getName()
                    );
                } else {
                    otherViewHolder.binding.attachmentSent.setVisibility(View.GONE);
                }

                val split = destination.split("@".toRegex()).toTypedArray()
                otherViewHolder.binding.txtDestination.text = split[0]
                otherViewHolder.binding.txtMessage.text = (chatModel.messageTxt)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("size------", chatModels?.size.toString())
        return chatModels?.size ?: 0
    }

    inner class MyViewHolder(val binding: AdapterMultimediaChatMeBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun onClick(v: View?) {
            mOnItemCliCkListener?.onItemClicked(adapterPosition)
        }

        init {
            binding.root.setOnClickListener(this)
        }
    }


    inner class OtherViewHolder(val binding: AdapterMultimediaChatBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun onClick(v: View?) {
            mOnItemCliCkListener?.onItemClicked(adapterPosition)
        }

        init {
            binding.root.setOnClickListener(this)
        }
    }

    fun setOnItemClickListener(mOnItemCliCkListener: OnItemCliCkListener) {
        this.mOnItemCliCkListener = mOnItemCliCkListener

    }

    private var mOnItemCliCkListener: OnItemCliCkListener? = null

    interface OnItemCliCkListener {
        fun onItemClicked(position: Int)
    }

    fun downloadfile(url: String?, attachment_sent: ImageView, filename: String?) {
        val progressCallback =
            TransferProgressListener { bytes, totalBytes ->
                Log.d(
                    "===========",
                    "Downloaded $bytes of $totalBytes bytes"
                )
            }
        val downloadCompleteListener: DownloadCompleteListener = object : DownloadCompleteListener {
            override fun downloadSuccess(path: String) {
                val bitmap = BitmapFactory.decodeFile(path)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                Glide.with(attachment_sent.context).asBitmap()
                    .load(stream.toByteArray())
                    .into(attachment_sent)
            }

            override fun downloadFail(error: String) {
                Toast.makeText(attachment_sent.context, error, Toast.LENGTH_LONG).show()
            }
        }
        val folder = DOWNLOAD_FOLDER + File.separator
        val handle: TransferRequestHandle = chatService.downloadAttachment(
            url,
            folder,
            filename,
            progressCallback,
            downloadCompleteListener
        )
    }


}