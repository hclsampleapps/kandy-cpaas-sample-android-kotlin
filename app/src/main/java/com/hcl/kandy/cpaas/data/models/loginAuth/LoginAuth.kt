package com.hcl.kandy.cpaas.data.models.loginAuth

import com.google.gson.annotations.SerializedName


class LoginAuth {
    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("expires_in")
    val expiresIn: Int? = null

    @SerializedName("refresh_expires_in")
    val refreshExpiresIn: Int? = null

    @SerializedName("refresh_token")
    val refreshToken: String? = null

    @SerializedName("token_type")
    val tokenType: String? = null

    @SerializedName("id_token")
    val idToken: String? = null

    @SerializedName("not-before-policy")
    val notBeforePolicy: Int? = null

    @SerializedName("session_state")
    val sessionState: String? = null

    @SerializedName("scope")
    val scope: String? = null
}