package com.jaehl.spaceTraders.data.services

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
) {
    private var token = ""

    fun getBearerToken() : String {
        return "$bearerToken $token"
    }

    fun setToken(token : String){
        this.token = token
    }

    companion object {
        private const val bearerToken = "Bearer"
    }
}