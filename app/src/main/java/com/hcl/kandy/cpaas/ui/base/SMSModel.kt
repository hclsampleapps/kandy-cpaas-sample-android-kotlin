package com.hcl.kandy.cpaas.ui.base

class SMSModel(
    val messageTxt: String, //detstination email address
    val destination: String, // true if message coming from other person
    val isMessageIn: Boolean, val messageId: String
)