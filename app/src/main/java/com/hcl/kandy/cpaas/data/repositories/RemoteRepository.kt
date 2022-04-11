package com.hcl.kandy.cpaas.data.repositories

import com.hcl.kandy.cpaas.data.models.loginAuth.LoginAuth
import com.hcl.kandy.cpaas.data.remote.ApiClient
import retrofit2.Call

class RemoteRepository {
    companion object {
        val instance = RemoteRepository()
    }

    fun userLogin(
        baseUrl: String,
        jsonObject: Map<String, String>
    ): Call<LoginAuth> {

        return ApiClient.getAuthApi(baseUrl).doLogin(jsonObject)
    }

}