package com.yawar.memo.domain.model

data class SearchRespone (
    val message : String,
    val pages_number : String,
    val status : String,
    val data : List<SearchModel>
        ) {}


