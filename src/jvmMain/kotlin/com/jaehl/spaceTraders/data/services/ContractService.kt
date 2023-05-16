package com.jaehl.spaceTraders.data.services

import com.jaehl.spaceTraders.data.model.Contract
import com.jaehl.spaceTraders.data.model.request.ContractDeliverRequest
import com.jaehl.spaceTraders.data.model.response.ContractDeliverResponse
import com.jaehl.spaceTraders.data.model.response.ContractFulfillResponse
import com.jaehl.spaceTraders.data.remote.ResponsePaged
import com.jaehl.spaceTraders.data.remote.SpaceTradersApi
import com.jaehl.spaceTraders.data.remote.baseBody
import com.jaehl.spaceTraders.data.remote.pagedBody
import javax.inject.Inject

interface ContractService {
    fun getContracts(page : Int) : ResponsePaged<Contract>
    fun getContract(contractId : String) : Contract
    fun contractAccept(contractId : String) : Contract
    fun contractDeliver(contractId : String, shipId : String, tradeSymbol : String, units : Int) : ContractDeliverResponse
    fun contractFulfill(contractId : String) : ContractFulfillResponse
}

class ContractServiceImp @Inject constructor(
    private val authService : AuthService,
    private val spaceTradersApi : SpaceTradersApi
) :ContractService {

    override fun getContracts(page: Int): ResponsePaged<Contract> {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.getContracts(
            bearerToken = bearerToken,
            limit = PageLimit,
            page = page
        ).pagedBody()
    }

    override fun getContract(contractId: String): Contract {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.getContract(
            bearerToken = bearerToken,
            contractId = contractId
        ).baseBody()
    }

    override fun contractAccept(contractId: String): Contract {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.contractAccept(
            bearerToken = bearerToken,
            contractId = contractId
        ).baseBody().contract
    }

    override fun contractDeliver(
        contractId: String,
        shipId: String,
        tradeSymbol: String,
        units: Int
    ): ContractDeliverResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.contractDeliver(
            bearerToken = bearerToken,
            contractId = contractId,
            data = ContractDeliverRequest(
                shipSymbol = shipId,
                tradeSymbol = tradeSymbol,
                units = units
            )
        ).baseBody()
    }

    override fun contractFulfill(contractId: String): ContractFulfillResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.contractFulfill(
            bearerToken = bearerToken,
            contractId = contractId
        ).baseBody()
    }

    companion object {
        private const val PageLimit = 20
    }
}