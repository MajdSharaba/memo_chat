package com.yawar.memo.domain.model

 data class SendContactNumberResponse(
     var id: String? = null,
     var name: String? = null,
     var number: String? = null,
     var image: String? = null,
     var state: String? = null,
     var chat_id: String? = null,
     var fcmToken: String? = null,
     var blockedFor: String? = null,
     var app_path: String? = null,
 )
