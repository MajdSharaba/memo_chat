package com.yawar.memo.network

import android.location.Location
import com.yawar.memo.model.Locationn
import retrofit2.http.GET
import retrofit2.http.Query

interface IpGeolocationService {
    @GET("ipgeo")
    suspend fun getLocation(@Query("apiKey") apiKey: String, @Query("ip") ipAddress: String): Locationn

    @GET("/")
    suspend fun getIpAddress(): String
}