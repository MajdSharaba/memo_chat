package com.yawar.memo.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
 data class SearchModel(

    var id: String? = null,
    var first_name: String? = null,
    var last_name: String? = null,
    var sn: String? = null,
    var image: String? = null,
    var phone: String? = null,
    var token: String? = null,
    var blocked_for: String? = null,
//    var isAdded: Boolean = false,
 ) : Cloneable, Parcelable {
     public override fun  clone() : SearchModel {
         var clone : SearchModel
         try {
             clone = super.clone() as SearchModel
         } catch ( e : CloneNotSupportedException) {
             throw  RuntimeException(e); //should not happen
         }
         return clone;
     }
}