package com.jaehl.spaceTraders.data.remote

import com.jaehl.spaceTraders.data.model.*
import com.jaehl.spaceTraders.data.model.request.RefineMaterialsRequest
import com.jaehl.spaceTraders.data.model.request.ShipNavigateRequest
import com.jaehl.spaceTraders.data.model.response.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SpaceTradersApi {

    @GET("v2/my/agent")
    fun getAgent(
        @Header("Authorization") bearerToken : String
    ) : Call<Response<Agent>>

    @GET("v2/my/ships")
    fun getShips(
        @Header("Authorization") bearerToken : String,
        @Query("limit") limit : Int,
        @Query("page") page : Int
    ) : Call<ResponsePaged<Ship>>

    @GET("v2/my/ships/{shipSymbol}")
    fun getShip(
        @Header("Authorization") bearerToken : String,
        @Path("shipSymbol") shipId : String
    ) : Call<Response<Ship>>

    @POST("v2/my/ships/{shipSymbol}/orbit")
    fun shipEnterOrbit(
        @Header("Authorization") bearerToken : String,
        @Path("shipSymbol") shipId : String
    ) : Call<Response<ShipNavUpdate>>

    @POST("v2/my/ships/{shipSymbol}/dock")
    fun shipDock(
        @Header("Authorization") bearerToken : String,
        @Path("shipSymbol") shipId : String
    ) : Call<Response<ShipNavUpdate>>

    @POST("v2/my/ships/{shipSymbol}/refuel")
    fun shipRefuel(
        @Header("Authorization") bearerToken : String,
        @Path("shipSymbol") shipId : String
    ) : Call<Response<RefuelResponse>>

    @POST("v2/my/ships/{shipSymbol}/refine")
    fun shipRefineMaterials(
        @Header("Authorization") bearerToken : String,
        @Path("shipSymbol") shipId : String,
        @Body data : RefineMaterialsRequest
    ) : Call<Response<RefineMaterialsResponse>>

    @POST("v2/my/ships/{shipSymbol}/extract")
    fun shipExtract(
        @Header("Authorization") bearerToken : String,
        @Path("shipSymbol") shipId : String
    ) : Call<Response<ExtractResponse>>

    @POST("v2/my/ships/{shipSymbol}/navigate")
    fun shipNavigate(
        @Header("Authorization") bearerToken : String,
        @Path("shipSymbol") shipId : String,
        @Body data : ShipNavigateRequest
    ) : Call<Response<ShipNavigateResponse>>

    @POST("v2/my/ships/{shipSymbol}/sell")
    fun shipSellCargo(
        @Header("Authorization") bearerToken : String,
        @Path("shipSymbol") shipId : String,
        @Body data : SymbolAmount
    ) : Call<Response<ShipSellCargoResponse>>

    @POST("v2/my/ships/{shipSymbol}/purchase")
    fun shipPurchaseCargo(
        @Header("Authorization") bearerToken : String,
        @Path("shipSymbol") shipId : String,
        @Body data : SymbolAmount
    ) : Call<Response<ShipPurchaseCargoResponse>>

    @POST("v2/my/ships/{shipSymbol}/jettison")
    fun shipJettisonCargo(
        @Header("Authorization") bearerToken : String,
        @Path("shipSymbol") shipId : String,
        @Body data : SymbolAmount
    ) : Call<Response<ShipJettisonCargoResponse>>

    @GET("v2/systems/{systemSymbol}")
    fun getSystem(
        @Header("Authorization") bearerToken : String,
        @Path("systemSymbol") systemSymbol : String
    ) : Call<Response<StarSystem>>

    @GET("v2/systems/{systemSymbol}/waypoints")
    fun getSystemWaypoints(
        @Header("Authorization") bearerToken : String,
        @Path("systemSymbol") systemSymbol : String,
        @Query("limit") limit : Int,
        @Query("page") page : Int
    ) : Call<ResponsePaged<SystemWaypoint>>

    @GET("v2/systems/{systemSymbol}/waypoints/{waypointSymbol}/Market")
    fun getMarket(
        @Header("Authorization") bearerToken : String,
        @Path("systemSymbol") systemSymbol : String,
        @Path("waypointSymbol") waypointSymbol : String
    ) : Call<Response<Market>>
}