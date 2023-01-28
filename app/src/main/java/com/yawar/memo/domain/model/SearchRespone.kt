package com.yawar.memo.domain.model

import kotlinx.android.parcel.Parcelize

@Parcelize
 data class SearchRespone(

     var id: String? = null,
     var name: String? = null,
     var SecretNumber: String? = null,
     var image: String? = null,
     var phone: String? = null,
     var token: String? = null,
     var blockedFor: String? = null,
     var isAdded: Boolean = false,
 ) : Cloneable  {
     public override fun  clone() : SearchRespone {
         var clone : SearchRespone
         try {
             clone = super.clone() as SearchRespone
         } catch ( e : CloneNotSupportedException) {
             throw  RuntimeException(e); //should not happen
         }
         return clone;
     }
}