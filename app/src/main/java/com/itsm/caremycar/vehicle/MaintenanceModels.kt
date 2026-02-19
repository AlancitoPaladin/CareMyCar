package com.itsm.caremycar.vehicle

import com.google.gson.annotations.SerializedName

data class MaintenanceRecord(
    val id: String,
    val serviceType: String?,
    val description: String?,
    val cost: Double?,
    val mileage: Int?,
    val serviceDate: String?
)

data class MaintenanceRecordDto(
    val id: String,
    @SerializedName("service_type")
    val serviceType: String?,
    val description: String?,
    val cost: Double?,
    val mileage: Int?,
    @SerializedName("service_date")
    val serviceDate: String?
)

data class MaintenanceListResponse(
    val items: List<MaintenanceRecordDto>
)

data class MaintenanceDetailResponse(
    val maintenance: MaintenanceRecordDto
)

data class MaintenanceDeleteResponse(
    val status: String
)

data class CreateMaintenanceRequest(
    @SerializedName("vehicle_id")
    val vehicleId: String,
    @SerializedName("service_type")
    val serviceType: String,
    @SerializedName("service_date")
    val serviceDate: String,
    val description: String? = null,
    val cost: Double? = null,
    val mileage: Int? = null
)

fun MaintenanceRecordDto.toMaintenanceRecord(): MaintenanceRecord {
    return MaintenanceRecord(
        id = id,
        serviceType = serviceType,
        description = description,
        cost = cost,
        mileage = mileage,
        serviceDate = serviceDate
    )
}
