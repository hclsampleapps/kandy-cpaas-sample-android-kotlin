package com.hcl.kandy.cpaas.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hcl.kandy.cpaas.data.models.Resource
import com.hcl.kandy.cpaas.data.models.loginAuth.LoginAuth
import com.hcl.kandy.cpaas.utils.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataRepository {
    companion object {
        val instance = DataRepository()
    }

    fun userLogin(baseUrl: String, jsonObject: Map<String, String>): LiveData<Resource<LoginAuth>> {
        val mutableTestData = MutableLiveData<Resource<LoginAuth>>()

        mutableTestData.value = (Resource.loading(null))

        RemoteRepository.instance.userLogin(baseUrl, jsonObject)
            .enqueue(object : Callback<LoginAuth> {

                override fun onFailure(call: Call<LoginAuth>, t: Throwable) {
                    mutableTestData.value = Resource.error(ErrorUtils.getError(t))
                }

                override fun onResponse(call: Call<LoginAuth>, response: Response<LoginAuth>) {
                    if (response.isSuccessful) {
                        mutableTestData.value = response.body()?.let { Resource.success(it) }
                    } else {
                        mutableTestData.value =
                            Resource.error(
                                ErrorUtils.getError(
                                    response.errorBody(),
                                    response.code()
                                )
                            )
                    }
                }
            })
        return mutableTestData

    }

}