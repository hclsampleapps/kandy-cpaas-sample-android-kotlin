package com.hcl.kandy.cpaas.ui.login

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.data.Status
import com.hcl.kandy.cpaas.data.models.Resource
import com.hcl.kandy.cpaas.data.models.loginAuth.LoginAuth
import com.hcl.kandy.cpaas.databinding.ActivityLoginBinding
import com.hcl.kandy.cpaas.ui.base.BaseActivity
import com.hcl.kandy.cpaas.utils.ProgressUtils
import com.hcl.kandy.cpaas.utils.Utils

class LoginActivity : BaseActivity() {

    private lateinit var mViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private var isPasswordGrantLoginType: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.buttonLogin.setOnClickListener(this)
        progressUtils = ProgressUtils(this)
        activity = this
        loadSignUp()
    }

    private fun loadSignUp() {
        binding.rgLoginTypeSelection
            .setOnCheckedChangeListener { group, checkedId ->
                if (checkedId == R.id.rb_password_grant) {
                    isPasswordGrantLoginType = true
                    binding.llPasswordGrant.setVisibility(View.VISIBLE)
                    binding.llClientCredentials.setVisibility(View.GONE)
                } else {
                    isPasswordGrantLoginType = false
                    binding.llClientCredentials.setVisibility(View.VISIBLE)
                    binding.llPasswordGrant.setVisibility(View.GONE)
                }
            }
    }

    private fun getLogin(allParams: Map<String, String>) {

        mViewModel.getLogin("https://" + (binding.etUrl.text.toString()), allParams)
            .observe(this, Observer<Resource<LoginAuth>> {
                when {
                    it?.status == Status.LOADING -> {
                        progressUtils.showDialog()
                    }
                    it?.status == Status.ERROR -> {
                        progressUtils.dismissDialog()
                        Utils.instance.showToast("" + it.error?.message, activity)
                    }
                    else -> {
                        progressUtils.dismissDialog()
                        val data = it?.data

                        if (data != null) {
                            if (data.accessToken != null) {
                                Utils.instance.showToast(
                                    "Success", activity
                                )
                                val b = Bundle()
                                b.putString(Utils.instance.access_token, data.accessToken)
                                b.putString(Utils.instance.id_token, data.idToken)
                                b.putString(Utils.instance.base_url, binding.etUrl.text.toString())
                                b.putBoolean(Utils.instance.login_type, isPasswordGrantLoginType)

                                goToActivity(activity, MainActivity::class.java, b)
                                finish()
                            } else {
                                Utils.instance.showToast(
                                    "Error", activity
                                )
                            }
                        }
                    }
                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_login -> {
                if (validateParameters(
                        isPasswordGrantLoginType,
                        binding.etUrl,
                        binding.etUserName,
                        binding.etUserPassword,
                        binding.etUserClient,
                        binding.etClientId,
                        binding.etClientSecret
                    )
                ) {

                    val obj = HashMap<String, String>()
                    if (isPasswordGrantLoginType) {
                        obj["username"] = binding.etUserName.text.toString()
                        obj["password"] = binding.etUserPassword.text.toString()
                        obj["grant_type"] = "password"
                        obj["scope"] = "openid"
                        obj["client_id"] = binding.etUserClient.text.toString()
                    } else {
                        obj["client_secret"] = binding.etClientSecret.text.toString()
                        obj["grant_type"] = "client_credentials"
                        obj["scope"] = "openid"
                        obj["client_id"] = binding.etClientId.text.toString()
                    }
                    getLogin(obj)
                }
            }
        }
    }

    private fun validateParameters(
        passwordGrantLoginType: Boolean,
        mBaseUrl: EditText,
        mEtUserName: EditText,
        mEtUserPassword: EditText,
        mEtClient: EditText,
        mClientId: EditText,
        mClientSecret: EditText
    ): Boolean {

        if (passwordGrantLoginType) {
            return when {
                mBaseUrl.text.isEmpty() -> {
                    mBaseUrl.error = "Please enter URL"
                    mBaseUrl.requestFocus()
                    false

                }
                mEtUserName.text.isEmpty() -> {
                    mEtUserName.error = "Please enter UserName"
                    mEtUserName.requestFocus()
                    false

                }
                mEtUserPassword.text.isEmpty() -> {
                    mEtUserPassword.error = "Please enter Password"
                    mEtUserPassword.requestFocus()
                    false

                }
                mEtClient.text.isEmpty() -> {
                    mEtClient.error = "Please enter Client ID"
                    mEtClient.requestFocus()
                    false

                }
                else -> true
            }
        } else {
            return when {
                mBaseUrl.text.isEmpty() -> {
                    mBaseUrl.error = "Please enter URL"
                    mBaseUrl.requestFocus()
                    false

                }
                mClientId.text.isEmpty() -> {
                    mClientId.error = "Please enter Client ID"
                    mClientId.requestFocus()
                    false

                }
                mClientSecret.text.isEmpty() -> {
                    mClientSecret.error = "Please enter Client Secret"
                    mClientSecret.requestFocus()
                    false

                }
                else -> true
            }
        }
        return true
    }


}
