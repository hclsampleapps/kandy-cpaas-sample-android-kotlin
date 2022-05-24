package com.hcl.kandy.cpaas.data.models

import com.rbbn.cpaas.mobile.messaging.api.Part;

class MultiMediaChatModel(
    val messageTxt: String,
    val destination: String,
    val isMessageIn: Boolean, val messageId: String, val parts: List<Part>
)