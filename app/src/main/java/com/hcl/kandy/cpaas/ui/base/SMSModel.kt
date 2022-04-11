package com.hcl.kandy.cpaas.ui.base

class SMSModel(
    val messageTxt: String,
    val destination: String,
    val isMessageIn: Boolean, val messageId: String
)