package com.jaehl.spaceTraders.data.services.mock

import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.util.DateHelperMock
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MockUtilTest {

    val dateHelper = DateHelperMock()
    @Test
    fun `removeCargo remove partial amount of cargo`(){

        val ship = Shipbuilder(dateHelper)
            .setSymbol("test")
            .setCargo(
                capacity = 10,
                inventory = listOf(
                    Ship.Cargo.InventoryItem(
                        symbol = "COPPER_ORE",
                        units = 5
                    ),
                    Ship.Cargo.InventoryItem(
                        symbol = "iron_ORE",
                        units = 2
                    )
                ),
            )
            .build()

        val cargo = MockUtil.removeCargo(
            cargo = ship.cargo,
            cargoId= "COPPER_ORE",
            units = 3)

        assertTrue(cargo is Ship.Cargo, "private removeCargo response is of type Cargo")
        assertEquals(4, cargo?.units)
        val copperCargo = cargo?.inventory?.firstOrNull{it.symbol == "COPPER_ORE" }
        assertEquals(2, copperCargo?.units)
        assertEquals("COPPER_ORE", copperCargo?.symbol)

    }

    @Test
    fun `removeCargo remove all cargo of one Type`(){

        val ship = Shipbuilder(dateHelper)
            .setSymbol("test")
            .setCargo(
                capacity = 10,
                inventory = listOf(
                    Ship.Cargo.InventoryItem(
                        symbol = "COPPER_ORE",
                        units = 5
                    ),
                    Ship.Cargo.InventoryItem(
                        symbol = "IRON_ORE",
                        units = 2
                    )
                ),
            )
            .build()

        val cargo = MockUtil.removeCargo(
            cargo = ship.cargo,
            cargoId= "COPPER_ORE",
            units = 5)

        assertTrue(cargo is Ship.Cargo, "private removeCargo response is of type Cargo")
        assertEquals(2, cargo?.units)
        val copperCargo = cargo?.inventory?.firstOrNull{it.symbol == "COPPER_ORE" }
        assertEquals(null, copperCargo, "copperCargo should be null")

    }

    @Test
    fun `removeCargo error`(){

        val ship = Shipbuilder(dateHelper)
            .setSymbol("test")
            .setCargo(
                capacity = 10,
                inventory = listOf(
                    Ship.Cargo.InventoryItem(
                        symbol = "COPPER_ORE",
                        units = 1
                    )
                ),
            )
            .build()

        var exception = assertThrows<Exception> {
            val cargo = MockUtil.removeCargo(
                cargo = ship.cargo,
                cargoId= "IRON_ORE",
                units = 5)
        }
        assertEquals("does not contain cargo IRON_ORE", exception.message)

        exception = assertThrows<Exception> {
            val cargo = MockUtil.removeCargo(
                cargo = ship.cargo,
                cargoId= "COPPER_ORE",
                units = 5)
        }
        assertEquals("can not remove more cargo than there is for COPPER_ORE", exception.message)
    }


    @Test
    fun `addCargo to current same type of cargo`(){

        val ship = Shipbuilder(dateHelper)
            .setSymbol("test")
            .setCargo(
                capacity = 10,
                inventory = listOf(
                    Ship.Cargo.InventoryItem(
                        symbol = "COPPER_ORE",
                        units = 3
                    ),
                    Ship.Cargo.InventoryItem(
                        symbol = "iron_ORE",
                        units = 2
                    )
                ),
            )
            .build()

        val cargo = MockUtil.addCargo(
            cargo = ship.cargo,
            cargoId = "COPPER_ORE",
            units = 2)

        assertTrue(cargo is Ship.Cargo, "private removeCargo response is of type Cargo")
        assertEquals(7, cargo?.units)
        val copperCargo = cargo?.inventory?.firstOrNull{it.symbol == "COPPER_ORE" }
        assertEquals(5, copperCargo?.units)
        assertEquals("COPPER_ORE", copperCargo?.symbol)
    }

    @Test
    fun `addCargo new type of cargo`(){

        val ship = Shipbuilder(dateHelper)
            .setSymbol("test")
            .setCargo(
                capacity = 10,
                inventory = listOf(
                    Ship.Cargo.InventoryItem(
                        symbol = "iron_ORE",
                        units = 2
                    )
                ),
            )
            .build()

        val cargo = MockUtil.addCargo(
            cargo = ship.cargo,
            cargoId = "COPPER_ORE",
            units = 2)

        assertTrue(cargo is Ship.Cargo, "private removeCargo response is of type Cargo")
        assertEquals(4, cargo?.units)
        val copperCargo = cargo?.inventory?.firstOrNull{it.symbol == "COPPER_ORE" }
        assertEquals(2, copperCargo?.units)
        assertEquals("COPPER_ORE", copperCargo?.symbol)
    }

    @Test
    fun `addCargo error`(){

        val ship = Shipbuilder(dateHelper)
            .setSymbol("test")
            .setCargo(
                capacity = 10,
                inventory = listOf(
                    Ship.Cargo.InventoryItem(
                        symbol = "COPPER_ORE",
                        units = 5
                    )
                ),
            )
            .build()

        var exception = assertThrows<Exception> {
            val cargo = MockUtil.addCargo(
                cargo = ship.cargo,
                cargoId = "IRON_ORE",
                units = 10)
        }
        assertEquals("can not add cargo over capacity", exception.message)
    }
}