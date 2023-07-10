package com.jaehl.spaceTraders.ui.pages.runTask.agents

import com.jaehl.spaceTraders.data.controllers.ShipController
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.repo.MarketRepo
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.BasicTaskInterface
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.MinerTaskInterface
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.TaskInterface
import com.jaehl.spaceTraders.util.DateHelper
import com.jaehl.spaceTraders.util.Logger


class StaticMinerAgent(
    private val shipController : ShipController,
    private val marketRepo: MarketRepo,
    private val dateHelper: DateHelper,
    private val logger: Logger
) : ShipAgent{

    override fun getShipController(): ShipController {
        return shipController
    }

    override fun getShip(): Ship {
        return shipController.getShip()
    }

    override fun setup(taskInterface : TaskInterface) {
        if (taskInterface !is MinerTaskInterface){
            return
        }
        stateDescription = "setup wait"
        shipController.enterOrbit()
        val asteroidWaypoint = taskInterface.retrieveAsteroidWaypoint()
        if(shipController.getShip().nav.waypointSymbol != asteroidWaypoint){
            shipController.navigate(asteroidWaypoint)
            stateDescription = "traveling to Asteroid field : $asteroidWaypoint"
        }
    }

    private fun canLocalSell(cargoId : String) : Boolean{
        val market = marketRepo.getMarketHistory(
            systemId = getShip().nav.systemSymbol,
            waypointId = getShip().nav.waypointSymbol
        ) ?: return false

        market.tradeGoods.firstOrNull{it.symbol == cargoId} ?: return false
        return true
    }

    override fun run(taskInterface : TaskInterface) {
        if (taskInterface !is MinerTaskInterface) return
        if (taskInterface !is BasicTaskInterface) return

        if (!shipController.getCoolDown().isFinished(dateHelper)) return


        shipController.calculateNavState()
        if (taskInterface.retrieveUseSurvey() && taskInterface.retrieveMiningSurvey().isExpired()) return

        var itemsToSell = arrayListOf<String>()
        if(getShip().cargo.isFull()){
            shipController.getShip().cargo.inventory.forEach { inventoryItem ->
                val cargoShip = taskInterface.findShipForCargo(
                    shipId = getShip().symbol,
                    systemId = getShip().nav.systemSymbol,
                    waypointId = getShip().nav.waypointSymbol,
                    cargoId = inventoryItem.symbol)
                if(cargoShip != null) {
                    if (cargoShip.getShip().nav.waypointSymbol == getShip().nav.waypointSymbol
                        && cargoShip.getShip().nav.status == Ship.Nav.Statue.InOrbit){
                        shipController.transferAllCargo(
                            shipController = cargoShip,
                            itemId = inventoryItem.symbol
                        )
                    }
                }
                else if(canLocalSell(inventoryItem.symbol)) {
                    itemsToSell.add(inventoryItem.symbol)
                }
                else {
                    logger.log("${shipController.getShip().symbol} jettison : ${inventoryItem.symbol}")
                    shipController.jettisonAllOfSingle(inventoryItem.symbol)
                }
            }
        }
        if(itemsToSell.isNotEmpty()) {
            shipController.dock()
            itemsToSell.forEach { itemId ->
                logger.log("${shipController.getShip().symbol} sell : ${itemId}")
                shipController.sellAll(itemId)
            }
            shipController.enterOrbit()
        }

        if(getShip().cargo.isFull()){
            stateDescription = "ship Full, waiting for cargo ship"
            return
        }
        shipController.extract(if(taskInterface.retrieveUseSurvey()) taskInterface.retrieveMiningSurvey() else null)
        stateDescription = "Minning at ${shipController.getShip().nav.waypointSymbol}"
    }

    override fun calculateState(taskInterface: TaskInterface) {

    }

    private var stateDescription : String = "Mining"

    override fun getAgentDetails(): AgentDetails {
        return AgentDetails(
            ship = getShip(),
            coolDown = shipController.getCoolDown(),
            stateDescription = stateDescription
        )
    }

    override fun acceptCargo(systemId: String, waypointId: String, cargoId: String): Boolean {
        return false
    }
}