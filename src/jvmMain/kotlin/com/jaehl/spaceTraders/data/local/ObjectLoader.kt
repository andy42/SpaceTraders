package com.jaehl.spaceTraders.data.local

import com.google.gson.Gson
import com.jaehl.spaceTraders.util.Logger
import java.io.File
import java.lang.reflect.Type
import javax.inject.Inject

interface ObjectLoader<T> {
    fun load(file : File) : T? //type IS "object : TypeToken<Array<CLASS>>() {}.type"
    fun save(file : File, data : T) : Boolean
}

class ObjectLoaderImp<T> @Inject constructor(
    private val logger: Logger,
    private val gson: Gson,
    //using Type as Gson can not use a generic as it's compiled to object at runtime
    private val type : Type, //type is "object : TypeToken<CLASS>() {}.type",
    private val showLoadErrors : Boolean = true
) : ObjectLoader<T> {


    override fun load(file : File) : T? {
        if(!file.exists()) {
            if(showLoadErrors){
                println("ERROR : ${file} does not exist\n${file.absoluteFile} ")
            }
            return null
        }
        //val gson = Gson().newBuilder().create()
        val fileString = file.inputStream().readBytes().toString(Charsets.UTF_8)
        return gson.fromJson<T>(fileString, type)
    }

    override fun save(file : File, data : T) : Boolean {
        return try {
            file.createNewFile()

            //var gson = GsonBuilder().setPrettyPrinting().create()
            var jsonString = gson.toJson(data)
            file.writeText(jsonString, Charsets.UTF_8)
            true
        } catch (t : Throwable){
            logger.error("ObjectLoaderImp "+t.message)
            false
        }
    }
}