package com.jaehl.spaceTraders.data.services

import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.SymbolAmount
import com.jaehl.spaceTraders.data.model.request.RefineMaterialsRequest
import com.jaehl.spaceTraders.data.model.request.ShipNavigateRequest
import com.jaehl.spaceTraders.data.model.response.*
import com.jaehl.spaceTraders.data.remote.*
import javax.inject.Inject

interface FleetService {
    fun getShips(page : Int) : ResponsePaged<Ship>
    fun getShip(shipId: String) : Ship
    fun shipEnterOrbit(shipId: String) : Ship.Nav
    fun shipDock(shipId: String) : Ship.Nav
    fun shipRefuel(shipId: String) : RefuelResponse
    fun shipRefineMaterials(shipId: String, produce : String) : RefineMaterialsResponse
    fun shipExtract(shipId: String): ExtractResponse
    fun shipNavigate(shipId: String, waypointId: String): ShipNavigateResponse
    fun shipSellCargo(shipId: String, cargoId: String, units : Int) : ShipSellCargoResponse
    fun shipPurchaseCargo(shipId: String, cargoId: String, units : Int) : ShipPurchaseCargoResponse
    fun shipJettisonCargo(shipId: String, cargoId: String, units : Int) : ShipJettisonCargoResponse
}

class FleetServiceImp @Inject constructor(
    private val authService : AuthService,
    private val spaceTradersApi : SpaceTradersApi
) : FleetService{

    override fun getShips(page : Int) : ResponsePaged<Ship> {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.getShips(bearerToken = bearerToken, page = page, limit = PageLimit).pagedBody()
    }

    override fun getShip(shipId: String): Ship {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.getShip(bearerToken = bearerToken, shipId = shipId).baseBody()
    }

    override fun shipEnterOrbit(shipId: String): Ship.Nav {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipEnterOrbit(bearerToken = bearerToken, shipId = shipId).baseBody().nav
    }

    override fun shipDock(shipId: String): Ship.Nav {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipDock(bearerToken = bearerToken, shipId = shipId).baseBody().nav
    }

    override fun shipRefuel(shipId: String): RefuelResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipRefuel(bearerToken = bearerToken, shipId = shipId).baseBody()
    }

    override fun shipRefineMaterials(shipId: String, produce : String): RefineMaterialsResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipRefineMaterials(
            bearerToken = bearerToken,
            shipId = shipId,
            data = RefineMaterialsRequest(
                produce = produce
            )
        ).baseBody()
    }

    override fun shipExtract(shipId: String): ExtractResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipExtract(bearerToken = bearerToken, shipId = shipId).baseBody()
    }

    override fun shipNavigate(shipId: String, waypointId: String): ShipNavigateResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipNavigate(
            bearerToken = bearerToken,
            shipId = shipId,
            data = ShipNavigateRequest(
                waypointSymbol = waypointId
            )
        ).baseBody()
    }

    override fun shipSellCargo(shipId: String, cargoId: String, units: Int): ShipSellCargoResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipSellCargo(
            bearerToken = bearerToken,
            shipId = shipId,
            data = SymbolAmount(
                symbol = cargoId,
                units = units
            )
        ).baseBody()
    }

    override fun shipPurchaseCargo(shipId: String, cargoId: String, units: Int): ShipPurchaseCargoResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipPurchaseCargo(
            bearerToken = bearerToken,
            shipId = shipId,
            data = SymbolAmount(
                symbol = cargoId,
                units = units
            )
        ).baseBody()
    }

    override fun shipJettisonCargo(shipId: String, cargoId: String, units : Int) : ShipJettisonCargoResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipJettisonCargo(
            bearerToken = bearerToken,
            shipId = shipId,
            data = SymbolAmount(
                symbol = cargoId,
                units = units
            )
        ).baseBody()
    }

    companion object {
        private const val PageLimit = 10
    }
}