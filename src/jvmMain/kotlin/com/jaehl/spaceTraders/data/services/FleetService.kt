package com.jaehl.spaceTraders.data.services

import com.jaehl.spaceTraders.data.model.MiningSurvey
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.SymbolAmount
import com.jaehl.spaceTraders.data.model.request.*
import com.jaehl.spaceTraders.data.model.response.*
import com.jaehl.spaceTraders.data.remote.*
import javax.inject.Inject

interface FleetService {
    fun getShips(page : Int) : ResponsePaged<Ship>
    fun getShip(shipId: String) : Ship
    fun getShipCoolDown(shipId: String) : Cooldown?
    fun shipEnterOrbit(shipId: String) : Ship.Nav
    fun shipDock(shipId: String) : Ship.Nav
    fun shipRefuel(shipId: String) : RefuelResponse
    fun shipRefineMaterials(shipId: String, produce : String) : RefineMaterialsResponse
    fun shipExtract(shipId: String): ExtractResponse
    fun shipExtract(shipId: String, miningSurvey : MiningSurvey): ExtractResponse
    fun shipNavigate(shipId: String, waypointId: String): ShipNavigateResponse
    fun shipWarp(shipId : String, waypointId: String) : ShipWarpResponse
    fun shipJump(shipId : String, systemId: String) : ShipJumpResponse
    fun shipSellCargo(shipId: String, cargoId: String, units : Int) : ShipSellCargoResponse
    fun shipPurchaseCargo(shipId: String, cargoId: String, units : Int) : ShipPurchaseCargoResponse
    fun shipJettisonCargo(shipId: String, cargoId: String, units : Int) : ShipJettisonCargoResponse
    fun shipMiningSurvey(shipId: String) : ShipMiningSurveyResponse
    fun shipTransferCargo(shipId: String, cargoId: String, units : Int, transferToShipSymbol : String) : Ship.Cargo
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

    override fun getShipCoolDown(shipId: String): Cooldown? {
        val bearerToken = authService.getBearerToken()
        val request = spaceTradersApi.getShipCoolDown(bearerToken = bearerToken, shipId = shipId)
        val response = request.execute()
        return if(response.code() == 204) null
        else if(response.isSuccessful) response.body()!!.data
        else throw Throwable(response.errorBody()?.string())
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

    override fun shipExtract(shipId: String, miningSurvey: MiningSurvey): ExtractResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipExtract(
            bearerToken = bearerToken,
            shipId = shipId,
            data = miningSurvey
        ).baseBody()
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

    override fun shipWarp(shipId: String, waypointId: String): ShipWarpResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipWarp(
            bearerToken = bearerToken,
            shipId = shipId,
            data = ShipWarpRequest(
                waypointSymbol = waypointId
            )
        ).baseBody()
    }

    override fun shipJump(shipId: String, systemId: String): ShipJumpResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipJump(
            bearerToken = bearerToken,
            shipId = shipId,
            data = ShipJumpRequest(
                systemSymbol = systemId
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

    override fun shipMiningSurvey(shipId: String): ShipMiningSurveyResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipMiningSurvey(
            bearerToken = bearerToken,
            shipId = shipId,
        ).baseBody()
    }

    override fun shipTransferCargo(shipId: String, cargoId: String, units: Int, transferToShipSymbol: String) : Ship.Cargo {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.shipTransferCargo(
            bearerToken = bearerToken,
            shipId = shipId,
            data = TransferCargoRequest(
                tradeSymbol = cargoId,
                units = units,
                shipSymbol = transferToShipSymbol
            )
        ).baseBody().cargo
    }

    companion object {
        private const val PageLimit = 10
    }
}