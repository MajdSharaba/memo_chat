package com.yawar.memo.domain.model

import kotlinx.android.parcel.Parcelize

@Parcelize
 data class CallHistoryModel (
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

    public override fun clone(): CallHistoryModel {

        var clone: CallHistoryModel
        try {
            clone = super.clone() as CallHistoryModel
        } catch (e: CloneNotSupportedException) {
            throw  RuntimeException(e); //should not happen
        }

        return clone;
    }
}

