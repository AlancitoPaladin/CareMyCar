package com.itsm.caremycar.api

import com.itsm.caremycar.session.LoginRequest
import com.itsm.caremycar.session.LoginResponse
import com.itsm.caremycar.session.RegisterRequest
import com.itsm.caremycar.session.RegisterResponse
import com.itsm.caremycar.vehicle.CreateMaintenanceRequest
import com.itsm.caremycar.vehicle.CatalogVehicleListResponse
import com.itsm.caremycar.vehicle.CreateVehicleRequest
import com.itsm.caremycar.vehicle.DeleteVehicleResponse
import com.itsm.caremycar.vehicle.MaintenanceDeleteResponse
import com.itsm.caremycar.vehicle.MaintenanceDetailResponse
import com.itsm.caremycar.vehicle.MaintenanceListResponse
import com.itsm.caremycar.vehicle.VehicleDetailResponse
import com.itsm.caremycar.vehicle.VehicleListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/vehicles")
    suspend fun listVehicles(): Response<VehicleListResponse>

    @GET("api/catalog/vehicles")
    suspend fun listCatalogVehicles(): Response<CatalogVehicleListResponse>

    @GET("api/vehicles/{vehicleId}")
    suspend fun getVehicleById(@Path("vehicleId") vehicleId: String): Response<VehicleDetailResponse>

    @GET("api/maintenance/{vehicleId}")
    suspend fun listMaintenanceByVehicle(@Path("vehicleId") vehicleId: String): Response<MaintenanceListResponse>

    @POST("api/maintenance")
    suspend fun createMaintenance(@Body request: CreateMaintenanceRequest): Response<MaintenanceDetailResponse>

    @PUT("api/maintenance/{maintenanceId}")
    suspend fun updateMaintenance(
        @Path("maintenanceId") maintenanceId: String,
        @Body payload: Map<String, @JvmSuppressWildcards Any>
    ): Response<MaintenanceDetailResponse>

    @DELETE("api/maintenance/{maintenanceId}")
    suspend fun deleteMaintenance(@Path("maintenanceId") maintenanceId: String): Response<MaintenanceDeleteResponse>

    @POST("api/vehicles")
    suspend fun createVehicle(@Body request: CreateVehicleRequest): Response<VehicleDetailResponse>

    @DELETE("api/vehicles/{vehicleId}")
    suspend fun deleteVehicle(@Path("vehicleId") vehicleId: String): Response<DeleteVehicleResponse>

    @PUT("api/vehicles/{vehicleId}")
    suspend fun updateVehicle(
        @Path("vehicleId") vehicleId: String,
        @Body payload: Map<String, @JvmSuppressWildcards Any>
    ): Response<VehicleDetailResponse>
}
