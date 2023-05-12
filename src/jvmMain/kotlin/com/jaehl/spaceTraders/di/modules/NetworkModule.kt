package com.jaehl.spaceTraders.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jaehl.spaceTraders.data.remote.SpaceTradersApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier


@Qualifier
public annotation class BaseUrl

@Module
class NetworkModule {

    @Provides
    fun okHttpClient() : OkHttpClient{
        return OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @BaseUrl
    @Provides
    fun baseUrl() : String {
        return "https://api.spacetraders.io/"
    }

    @Provides
    fun gson() : Gson{
        return GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ")
            .create()
    }

    @Provides
    fun retrofit(@BaseUrl baseUrl : String, okHttpClient : OkHttpClient, gson : Gson) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            //.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun spaceTradersApi(retrofit : Retrofit) : SpaceTradersApi {
        return retrofit.create(SpaceTradersApi::class.java)
    }
}