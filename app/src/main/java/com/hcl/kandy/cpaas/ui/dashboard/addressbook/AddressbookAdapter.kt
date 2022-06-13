package com.hcl.kandy.cpaas.ui.dashboard.addressbook

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.databinding.AdapterAddressbookBinding
import com.rbbn.cpaas.mobile.addressbook.model.Contact


class AddressbookAdapter(contactModels: ArrayList<Contact>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {


    companion object {
        private const val CONTACT_TYPE = 1
    }

    private val contactModels: ArrayList<Contact> = contactModels

    override fun getItemViewType(position: Int): Int {
        return CONTACT_TYPE
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<AdapterAddressbookBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.adapter_addressbook, viewGroup, false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        val contactModel: Contact = contactModels[i]
        val myViewHolder = holder as AddressbookAdapter.MyViewHolder
        myViewHolder.binding.txtTitle.text = contactModel.firstName
        myViewHolder.binding.txtMessage.text = contactModel.emailAddress
        myViewHolder.binding.addressbookContainer.setTag(R.id.addressbookContainer, contactModel)
        myViewHolder.binding.deleteButton.setTag(R.id.addressbookContainer, contactModel)


    }

    override fun getItemCount(): Int {
        Log.d("size------", contactModels?.size.toString())
        return contactModels?.size ?: 0
    }

    inner class MyViewHolder(val binding: AdapterAddressbookBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {


//        title = itemView.findViewById(R.id.txtTitle)
//        message = itemView.findViewById(R.id.txtMessage)
//        addressbookContainer = itemView.findViewById(R.id.addressbookContainer)
//        deleteButton = itemView.findViewById(R.id.deleteButton)
//        addressbookContainer.setOnClickListener { v: View ->
//            val contact = v.getTag(R.id.addressbookContainer) as Contact
//            addressbookListner.onClickAddressBook(contact)
//        }
//        deleteButton.setOnClickListener { v: View ->
//            val contact = v.getTag(R.id.addressbookContainer) as Contact
//            addressbookListner.onDeleteAddressBook(contact)
//        }


        override fun onClick(v: View?) {
            mOnItemCliCkListener?.onItemClicked(adapterPosition, v)
        }


        init {
            binding.addressbookContainer.setOnClickListener(this)
            binding.deleteButton.setOnClickListener(this)
            binding.root.setOnClickListener(this)
        }
    }

    fun SetOnItemClickListener(mOnItemCliCkListener: OnItemCliCkListener?) {
        this.mOnItemCliCkListener = mOnItemCliCkListener

    }

    private var mOnItemCliCkListener: OnItemCliCkListener? = null

    interface OnItemCliCkListener {
        fun onItemClicked(position: Int, v: View?)

    }

}