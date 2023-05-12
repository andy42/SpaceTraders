package com.jaehl.spaceTraders.data.local

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jaehl.spaceTraders.util.Logger
import java.io.File
import java.lang.reflect.Type

interface ObjectListLoader<T> {
    fun load(file : File) : List<T> //type IS "object : TypeToken<Array<CLASS>>() {}.type"
    fun save(file : File, objects : List<T>) : Boolean
}

class  ObjectListJsonLoader<T>(
    private val logger: Logger,
    //using Type as Gson can not use a generic as it's compiled to object at runtime
    private val type : Type, //type is "object : TypeToken<Array<CLASS>>() {}.type"
) : ObjectListLoader<T> {

    override fun load(file : File) : List<T> {
        if(!file.exists()) {
            println("ERROR : ${file} does not exist\n${file.absoluteFile} ")
            return listOf()
        }
        val gson = Gson().newBuilder().create()
        val fileString = file.inputStream().readBytes().toString(Charsets.UTF_8)
        return gson.fromJson<Array<T>>(fileString, type).toList()
    }

    override fun save(file : File, data : List<T>) : Boolean {
        return try {
            file.createNewFile()

            var gson = GsonBuilder().setPrettyPrinting().create()
            var jsonString = gson.toJson(data)
            file.writeText(jsonString, Charsets.UTF_8)
            true
        } catch (t : Throwable){
            logger.error("ObjectListJsonLoader "+t.message)
            false
        }
    }
}