package com.jaehl.spaceTraders.data.repo

import com.jaehl.spaceTraders.data.local.LocalFileConfig
import com.jaehl.spaceTraders.data.local.ObjectListLoader
import com.jaehl.spaceTraders.data.model.CargoData
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File
import javax.inject.Inject

class CargoInfoRepo @Inject constructor(
    private val logger : Logger,
    private val cargoDataLoader : ObjectListLoader<CargoData>,
    localFileConfig : LocalFileConfig
) {

    private val file : File = localFileConfig.getCargoInfoFile()

    private val cargoMap = LinkedHashMap<String, CargoData>()
    private var loaded = false

    init {
        GlobalScope.async {
            loadLocal(true)
        }
    }

    fun getCargoData(symbol : String) : CargoData? {
        return cargoMap[symbol]
    }

    fun getCargoData(inventoryItem : Ship.Cargo.InventoryItem,) : CargoData {
        if(!cargoMap.containsKey(inventoryItem.symbol)){
            cargoMap[inventoryItem.symbol] = CargoData.create(inventoryItem)
            cargoDataLoader.save(file, cargoMap.values.toList())
        }
        return cargoMap[inventoryItem.symbol]!!
    }

    fun updateCargoData(cargoData : CargoData) {
        cargoMap[cargoData.symbol] = cargoData
        cargoDataLoader.save(file, cargoMap.values.toList())
    }

    fun deleteCargoData(symbol : String) {
        cargoMap.remove(symbol)
        cargoDataLoader.save(file, cargoMap.values.toList())
    }

    private fun loadLocal(forceReload : Boolean = false){
        if(loaded && !forceReload) return
        try {
            cargoMap.clear()
            cargoDataLoader.load(file).forEach {
                cargoMap[it.symbol] = it
            }
            cargoDataLoader.save(file, cargoMap.values.toList())
        } catch (t : Throwable){
            logger.error("CargoInfoRepo ${t.message}")
        }
    }
}