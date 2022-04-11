package com.hcl.kandy.cpaas.data.remote

import com.hcl.kandy.cpaas.data.models.loginAuth.LoginAuth
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiEndPoint {

    @FormUrlEncoded
    @POST("cpaas/auth/v1/token")
    fun doLogin(@FieldMap dataModal: Map<String, String>): Call<LoginAuth>

}