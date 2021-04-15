package com.dmj.tawktodevtest.data.network

import com.dmj.tawktodevtest.data.models.Profile
import com.dmj.tawktodevtest.data.models.User
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @GET("users")
    fun getUserList(@Header("Authorization") token: String, @Query("since") since: Int): Call<ArrayList<User>>

    @GET("users/{username}")
    fun getUserProfile(@Header("Authorization") token: String, @Path("username") username: String): Call<Profile>
}