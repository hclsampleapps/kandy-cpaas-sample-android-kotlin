package com.hcl.kandy.cpaas.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hcl.kandy.cpaas.data.models.Resource
import com.hcl.kandy.cpaas.data.models.loginAuth.LoginAuth
import com.hcl.kandy.cpaas.data.repositories.DataRepository

class LoginViewModel : ViewModel() {
    private var getLoginAuth: LiveData<Resource<LoginAuth>>? = null

    fun getLogin(baseUrl: String, body: Map<String, String>): LiveData<Resource<LoginAuth>> {
        getLoginAuth = DataRepository.instance.userLogin(baseUrl, body)
        return getLoginAuth as LiveData<Resource<LoginAuth>>
    }

}