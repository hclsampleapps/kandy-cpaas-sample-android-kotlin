package com.hcl.kandy.cpaas.ui.dashboard.addressbook

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.databinding.FragmentAddressbookBinding
import com.hcl.kandy.cpaas.ui.base.BaseFragment
import com.hcl.kandy.cpaas.ui.login.MainActivity
import com.rbbn.cpaas.mobile.CPaaS
import com.rbbn.cpaas.mobile.addressbook.api.AddressBookService
import com.rbbn.cpaas.mobile.addressbook.api.DeleteContactCallback
import com.rbbn.cpaas.mobile.addressbook.api.RetrieveContactsCallback
import com.rbbn.cpaas.mobile.addressbook.model.Contact
import com.rbbn.cpaas.mobile.utilities.exception.MobileError

class AddressBookList : BaseFragment(), AddressbookAdapter.OnItemCliCkListener {

    private lateinit var binding: FragmentAddressbookBinding

    private lateinit var mAddressBookService: AddressBookService
    private lateinit var mContactList: ArrayList<Contact>
    private lateinit var homeAdapter: AddressbookAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_addressbook, container, false)

        binding.addContact.setOnClickListener(this)

        mContactList = ArrayList()
        homeAdapter = AddressbookAdapter(mContactList)
        binding.adapter = homeAdapter
        homeAdapter.SetOnItemClickListener(this)

        initAddressBookService()
        getAllContact()

        return binding.root
    }

    private fun getAllContact() {
        mAddressBookService!!.retrieveContactList("default", object : RetrieveContactsCallback {
            override fun onSuccess(list: List<Contact>) {
                Log.d("HCL", "Get the list of conatct")
                mContactList.clear()
                for (item in list) {
                    mContactList.add(item)
                    Log.d("HCL", item.emailAddress)
                }
                notifyList()
            }

            override fun onFail(mobileError: MobileError) {
                Log.d("HCL", "fail list of conatct")
            }
        })
    }

    private fun initAddressBookService() {
        val mainActivityContext: MainActivity = activity as MainActivity
        val cpaas: CPaaS = mainActivityContext.getCpaas()
        mAddressBookService = cpaas.addressBookService
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.add_contact -> addContact()
        }
    }

    private fun addContact() {
        addFragment(AddContactActivity())
//        startActivity(Intent(activity, AddContactActivity::class.java))
    }

    private fun notifyList() {
        val context = context
        if (context != null) {
            val mainHandler = Handler(context.mainLooper)

            // This is your code
            val myRunnable = Runnable {
                Log.d("HCL", "notify list")
                homeAdapter.notifyDataSetChanged()
            }
            mainHandler.post(myRunnable)
        }
    }

    fun onClickAddressBook(contact: Contact) {

        val bundle = Bundle()
        bundle.putString("primaryContact", contact.primaryContact)
        bundle.putString("firstName", contact.firstName)
        bundle.putString("lastName", contact.lastName)
        bundle.putString("email", contact.emailAddress)
        bundle.putString("buisPNo", contact.businessPhoneNumber)
        bundle.putString("homePNo", contact.homePhoneNumber)
        bundle.putString("mobilePNo", contact.mobilePhoneNumber)
        bundle.putString("contactId", contact.contactId)
        bundle.putBoolean("update", true)
        val f: Fragment = AddContactActivity()
        f.arguments = bundle
        addFragment(f)
//        intent.putExtras(bundle)
//        startActivity(intent)
    }

    fun onDeleteAddressBook(contact: Contact) {
        mAddressBookService!!.deleteContact(
            contact.contactId,
            "default",
            object : DeleteContactCallback {
                override fun onSuccess() {
                    showToastS("Contact Deleted successfully")
//                    hideProgressBAr()
                    Log.d("HCL", "Addressbook contact delete success")
                    getAllContact()
                }

                override fun onFail(mobileError: MobileError) {
                    showToastS("Contact Deleted failed")
//                    hideProgressBAr()
                    Log.d("HCL", "Addressbook contact delete failed")
                }
            })
    }

    override fun onItemClicked(position: Int, v: View?) {
        if (v?.id == R.id.deleteButton) {
            onDeleteAddressBook(mContactList.get(position))
        } else {
            onClickAddressBook(mContactList.get(position))
        }
    }


}