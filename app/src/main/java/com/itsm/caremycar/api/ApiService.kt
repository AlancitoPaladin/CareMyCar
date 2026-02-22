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
import com.itsm.caremycar.vehicle.MaintenanceRecommendationsResponse
import com.itsm.caremycar.vehicle.MaintenanceUpcomingResponse
import com.itsm.caremycar.vehicle.CancelServiceOrderRequest
import com.itsm.caremycar.vehicle.CompleteServiceOrderRequest
import com.itsm.caremycar.vehicle.CreateServiceOrderRequest
import com.itsm.caremycar.vehicle.ServiceOrderDetailResponse
import com.itsm.caremycar.vehicle.ServiceOrderListResponse
import com.itsm.caremycar.vehicle.ServiceOrderQuoteResponse
import com.itsm.caremycar.vehicle.StartServiceOrderRequest
import com.itsm.caremycar.vehicle.VehicleDetailResponse
import com.itsm.caremycar.vehicle.VehicleListResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

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

    @GET("api/maintenance/insights/recommendations/{vehicleId}")
    suspend fun getMaintenanceRecommendations(
        @Path("vehicleId") vehicleId: String
    ): Response<MaintenanceRecommendationsResponse>

    @GET("api/maintenance/insights/upcoming")
    suspend fun getMaintenanceUpcoming(): Response<MaintenanceUpcomingResponse>

    @GET("api/maintenance/insights/upcoming/all")
    suspend fun getMaintenanceUpcomingAll(): Response<MaintenanceUpcomingResponse>

    @POST("api/maintenance")
    suspend fun createMaintenance(@Body request: CreateMaintenanceRequest): Response<MaintenanceDetailResponse>

    @PUT("api/maintenance/{maintenanceId}")
    suspend fun updateMaintenance(
        @Path("maintenanceId") maintenanceId: String,
        @Body payload: Map<String, @JvmSuppressWildcards Any>
    ): Response<MaintenanceDetailResponse>

    @DELETE("api/maintenance/{maintenanceId}")
    suspend fun deleteMaintenance(@Path("maintenanceId") maintenanceId: String): Response<MaintenanceDeleteResponse>

    @POST("api/service-orders")
    suspend fun createServiceOrder(@Body request: CreateServiceOrderRequest): Response<ServiceOrderDetailResponse>

    @POST("api/service-orders/quote/{vehicleId}")
    suspend fun getServiceOrderQuote(
        @Path("vehicleId") vehicleId: String,
        @Body payload: Map<String, @JvmSuppressWildcards String>
    ): Response<ServiceOrderQuoteResponse>

    @GET("api/service-orders/my")
    suspend fun listMyServiceOrders(): Response<ServiceOrderListResponse>

    @GET("api/service-orders")
    suspend fun listAllServiceOrders(
        @Query("status") status: String? = null
    ): Response<ServiceOrderListResponse>

    @PATCH("api/service-orders/{orderId}/start")
    suspend fun startServiceOrder(
        @Path("orderId") orderId: String,
        @Body request: StartServiceOrderRequest
    ): Response<ServiceOrderDetailResponse>

    @PATCH("api/service-orders/{orderId}/complete")
    suspend fun completeServiceOrder(
        @Path("orderId") orderId: String,
        @Body request: CompleteServiceOrderRequest
    ): Response<ServiceOrderDetailResponse>

    @PATCH("api/service-orders/{orderId}/cancel")
    suspend fun cancelServiceOrder(
        @Path("orderId") orderId: String,
        @Body request: CancelServiceOrderRequest
    ): Response<ServiceOrderDetailResponse>

    @Streaming
    @GET("api/service-orders/report")
    suspend fun downloadServiceOrdersReport(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("status") status: String? = "FINALIZADO"
    ): Response<ResponseBody>

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
