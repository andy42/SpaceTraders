package com.jaehl.spaceTraders.data.remote

import retrofit2.Call

data class Response<T>(
    val data : T
)

data class ResponsePaged<T>(
    val data : List<T>,
    val meta : ResponsePageMeta
)

data class ResponsePageMeta(
    val total : Int = 0,
    val page : Int = 0,
    val limit : Int = 10
)

fun <R, BR : Response<R>> Call<BR>.baseBody(): R {
    val response = execute()

    if(response.isSuccessful) return response.body()!!.data
    else throw Throwable(response.errorBody()?.string())
}

fun <R, BR : ResponsePaged<R>> Call<BR>.pagedBody(): ResponsePaged<R> {
    val response = execute()

    if(response.isSuccessful) return response.body()!!
    else if (response.code() == 404) {
        throw ResourceNotFound(response.errorBody()?.string() ?: "not found")
    }
    else throw Throwable(response.errorBody()?.string())
}

class ResourceNotFound(message : String) : Exception(message)

class BadRequest(message : String) : Exception(message)