package com.hcl.kandy.cpaas.ui.dashboard.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.databinding.FragmentCallBinding
import com.hcl.kandy.cpaas.ui.base.BaseFragment

class CallFragment : BaseFragment() {
    private lateinit var binding: FragmentCallBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call, container, false)
        binding.startCallButton.setOnClickListener(this)
        return binding.root

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.start_call_button -> {
                startCall()
            }
        }
    }

    private fun startCall() {
        val videoChecked: Boolean
        when (binding.radioButtonGroup.getCheckedRadioButtonId()) {
            R.id.radio_video -> {
                videoChecked = true
            }
            else -> {
                videoChecked = false
            }
        }
        val callee: String = binding.participantAddress.text.toString()
        val bundle = Bundle()
        bundle.putBoolean("isVideoChecked", videoChecked)
        bundle.putBoolean("placeCall", true)
        bundle.putString("DestinationAddress", callee)

        val f: Fragment = StartCallFragment()
        f.arguments = bundle
        addFragment(f)

    }

}