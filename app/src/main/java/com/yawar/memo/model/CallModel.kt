package com.yawar.memo.model

import kotlinx.android.parcel.Parcelize

@Parcelize
 data class CallModel (
     var id: String = "",
     var username: String = "",
     var caller_id: String? = "",
     var image: String = "",
     var call_type: String  = "",
     var answer_id: String  = "",
     var call_status: String = "",
     var duration: String = "",
     var createdAt: String = "",
): Cloneable {

    public override fun clone(): CallModel {

        var clone: CallModel
        try {
            clone = super.clone() as CallModel
        } catch (e: CloneNotSupportedException) {
            throw  RuntimeException(e); //should not happen
        }

        return clone;
    }
}

