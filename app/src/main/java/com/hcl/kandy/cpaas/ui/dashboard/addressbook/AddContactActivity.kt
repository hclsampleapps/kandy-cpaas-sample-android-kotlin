package com.hcl.kandy.cpaas.ui.dashboard.addressbook

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.databinding.ActivityAddContactBinding
import com.hcl.kandy.cpaas.ui.base.BaseFragment
import com.hcl.kandy.cpaas.ui.login.MainActivity
import com.rbbn.cpaas.mobile.addressbook.api.AddContactCallback
import com.rbbn.cpaas.mobile.addressbook.api.AddressBookService
import com.rbbn.cpaas.mobile.addressbook.api.UpdateContactCallback
import com.rbbn.cpaas.mobile.addressbook.model.Contact
import com.rbbn.cpaas.mobile.utilities.exception.MobileError

class AddContactActivity : BaseFragment(), View.OnClickListener {
    private var mAddressBookService: AddressBookService? = null
    private var mUpdateContact = false
    private var mUpdateContactId: String? = null

    private lateinit var binding: ActivityAddContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_add_contact, container, false)

        binding.buttonCreateContact.setOnClickListener(this)
        initAddressBookService()
        return binding.root
    }


    private fun initAddressBookService() {
        mAddressBookService = (activity as MainActivity).getCpaas().addressBookService
        var extras: Bundle = Bundle()
        if (arguments != null) {
            extras = arguments as Bundle
            if (extras.containsKey("update")) {
                binding.etPrimaryContact!!.setText(extras.getString("primaryContact", ""))
                binding.etFirstName!!.setText(extras.getString("firstName", ""))
                binding.etLastName!!.setText(extras.getString("lastName", ""))
                binding.etEmail!!.setText(extras.getString("email", ""))
                binding.etBPhoneNo!!.setText(extras.getString("buisPNo", ""))
                binding.etHPhoneNo!!.setText(extras.getString("homePNo", ""))
                binding.etMPhoneNo!!.setText(extras.getString("mobilePNo", ""))
                mUpdateContactId = extras.getString("contactId", "")
                mUpdateContact = true
                binding.buttonCreateContact.text = "Update Contact"
            }
        }

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.button_create_contact -> {
                val prContact = binding.etPrimaryContact!!.text.toString()
                if (TextUtils.isEmpty(prContact)) {
                    showToastS("Please add primary contact")
                } else {
                    if (mUpdateContact) updateContact() else addContact()
                }
            }
        }
    }

    private fun updateContact() {
        val contact = Contact(mUpdateContactId)
        contact.primaryContact = binding.etPrimaryContact!!.text.toString()
        contact.firstName = binding.etFirstName!!.text.toString()
        contact.lastName = binding.etLastName!!.text.toString()
        contact.emailAddress = binding.etEmail!!.text.toString()
        contact.businessPhoneNumber = binding.etBPhoneNo!!.text.toString()
        contact.homePhoneNumber = binding.etHPhoneNo!!.text.toString()
        contact.mobilePhoneNumber = binding.etMPhoneNo!!.text.toString()
        contact.isBuddy = true
        mAddressBookService!!.updateContact(contact, "default", object : UpdateContactCallback {
            override fun onSuccess(contact: Contact) {
                showToastS("Contact Updated successfully")
                Log.d("HCL", "Addressbook contact update success")
            }

            override fun onFail(mobileError: MobileError) {
                showToastS("Contact Update Failed")
                Log.d("HCL", "Addressbook update add fail")
            }
        })
    }

    private fun addContact() {
        val contact = Contact()
        contact.primaryContact = binding.etPrimaryContact!!.text.toString()
        contact.firstName = binding.etFirstName!!.text.toString()
        contact.lastName = binding.etLastName!!.text.toString()
        contact.emailAddress = binding.etEmail!!.text.toString()
        contact.businessPhoneNumber = binding.etBPhoneNo!!.text.toString()
        contact.homePhoneNumber = binding.etHPhoneNo!!.text.toString()
        contact.mobilePhoneNumber = binding.etMPhoneNo!!.text.toString()
        contact.isBuddy = true
        mAddressBookService!!.addContact(contact, "default", object : AddContactCallback {
            override fun onSuccess(contact: Contact) {
                showToastS("Contact Added successfully")
                Log.d("HCL", "Addressbook contact add success")
            }

            override fun onFail(mobileError: MobileError) {
                showToastS("Contact Added Failed")
                Log.d("HCL", "Addressbook contact add fail")
            }
        })
    }
}