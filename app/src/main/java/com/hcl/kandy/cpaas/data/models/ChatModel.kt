package com.hcl.kandy.cpaas.data.models

class ChatModel(
    val messageTxt: String,
    val destination: String,
    val isMessageIn: Boolean, val messageId: String
)