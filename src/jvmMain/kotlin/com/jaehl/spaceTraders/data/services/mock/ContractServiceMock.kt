package com.jaehl.spaceTraders.data.services.mock

import com.jaehl.spaceTraders.data.model.Agent
import com.jaehl.spaceTraders.data.model.Contract
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.response.ContractDeliverResponse
import com.jaehl.spaceTraders.data.model.response.ContractFulfillResponse
import com.jaehl.spaceTraders.data.remote.ResourceNotFound
import com.jaehl.spaceTraders.data.remote.ResponsePageMeta
import com.jaehl.spaceTraders.data.remote.ResponsePaged
import com.jaehl.spaceTraders.data.services.ContractService

class ContractServiceMock(
    var agent : Agent,
    var shipMap : HashMap<String, Ship>,
    private val contractMap : HashMap<String, Contract>

) : ContractService {

    override fun getContracts(page: Int): ResponsePaged<Contract> {
        return ResponsePaged(
            data = contractMap.values.toList(),
            meta = ResponsePageMeta(
                total = contractMap.values.size,
                page = 1,
                limit = contractMap.values.size
            )
        )
    }

    override fun getContract(contractId: String): Contract {
        return contractMap[contractId] ?: throw ResourceNotFound("contract not found : $contractId")
    }

    override fun contractAccept(contractId: String): Contract {
        var contract = contractMap[contractId] ?: throw ResourceNotFound("contract not found : $contractId")
        contract = contract.copy(
            accepted = true
        )
        contractMap[contractId] = contract

        return contract
    }

    override fun contractDeliver(
        contractId: String,
        shipId: String,
        tradeSymbol: String,
        units: Int
    ): ContractDeliverResponse {
        var contract = contractMap[contractId] ?: throw ResourceNotFound("contract not found : $contractId")

        contractMap[contractId] = contract

        var ship = shipMap[shipId] ?: throw ResourceNotFound("ship not found : $shipId")

        ship = ship.copy(
            cargo = MockUtil.removeCargo(ship.cargo, tradeSymbol, units)
        )
        shipMap[shipId] = ship

        return ContractDeliverResponse(
            contract = contract,
            cargo = ship.cargo
        )
    }

    override fun contractFulfill(contractId: String): ContractFulfillResponse {
        var contract = contractMap[contractId] ?: throw ResourceNotFound("contract not found : $contractId")
        contract = contract.copy(
            fulfilled = true
        )
        contractMap[contractId] = contract

        return ContractFulfillResponse(
            agent = agent,
            contract = contract
        )
    }
}