package com.hcl.kandy.cpaas.ui.dashboard.sms

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.databinding.AdapterChatBinding
import com.hcl.kandy.cpaas.databinding.AdapterChatMeBinding
import com.hcl.kandy.cpaas.ui.base.SMSModel

class SMSAdapter(smsModels: ArrayList<SMSModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private val smsModels: ArrayList<SMSModel> = smsModels

    override fun getItemViewType(position: Int): Int {
        val smsModels: SMSModel = smsModels[position]
        return if (smsModels.isMessageIn) CHAT_TYPE_OTHER else CHAT_TYPE_ME
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == CHAT_TYPE_ME) {
            val binding = DataBindingUtil.inflate<AdapterChatBinding>(
                LayoutInflater.from(viewGroup.context),
                R.layout.adapter_chat, viewGroup, false
            )
            MyViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<AdapterChatMeBinding>(
                LayoutInflater.from(viewGroup.context),
                R.layout.adapter_chat_me, viewGroup, false
            )
            OtherViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        val smsModel: SMSModel = smsModels[i]
        val destination: String = smsModel.destination
        when (holder.getItemViewType()) {
            CHAT_TYPE_ME -> {
                val myViewHolder = holder as MyViewHolder
                myViewHolder.binding.txtDestination.text = destination
                myViewHolder.binding.txtMessage.text = (smsModel.messageTxt)
            }
            CHAT_TYPE_OTHER -> {
                val otherViewHolder = holder as OtherViewHolder
                val split = destination.split("@".toRegex()).toTypedArray()
                otherViewHolder.binding.txtDestination.text = split[0]
                otherViewHolder.binding.txtMessage.text = (smsModel.messageTxt)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("size------", smsModels?.size.toString())
        return smsModels?.size ?: 0
    }

    inner class MyViewHolder(val binding: AdapterChatBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun onClick(v: View?) {
            mOnItemCliCkListener?.onItemClicked(adapterPosition)
        }

        init {
            binding.root.setOnClickListener(this)
        }
    }


    inner class OtherViewHolder(val binding: AdapterChatMeBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun onClick(v: View?) {
            mOnItemCliCkListener?.onItemClicked(adapterPosition)
        }

        init {
            binding.root.setOnClickListener(this)
        }
    }

    private var mOnItemCliCkListener: OnItemCliCkListener? = null

    interface OnItemCliCkListener {
        fun onItemClicked(position: Int)

    }

    private val CHAT_TYPE_ME = 1
    private val CHAT_TYPE_OTHER = 2
}