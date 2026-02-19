package com.itsm.caremycar.vehicle

import com.google.gson.annotations.SerializedName

data class Vehicle(
    val id: String,
    val make: String?,
    val model: String?,
    val year: Int?,
    val color: String?,
    val vehicleType: String?,
    val transmission: String?,
    val fuelType: String?,
    val currentMileage: Double?,
    val imageUrls: List<String>
) {
    val title: String
        get() = listOfNotNull(make, model).joinToString(" ").ifBlank { "Vehiculo sin nombre" }

    val subtitle: String
        get() = buildString {
            year?.let { append(it) }
            if (isNotBlank() && !color.isNullOrBlank()) append(" · ")
            color?.takeIf { it.isNotBlank() }?.let { append(it) }
            if (isBlank()) append("Sin datos generales")
        }
}

data class VehicleListResponse(
    val items: List<VehicleDto>
)

data class VehicleDetailResponse(
    val vehicle: VehicleDto
)

data class DeleteVehicleResponse(
    val status: String
)

data class VehicleDto(
    val id: String,
    val make: String?,
    val model: String?,
    val year: Int?,
    val color: String?,
    @SerializedName("vehicle_type")
    val vehicleType: String?,
    val transmission: String?,
    @SerializedName("fuel_type")
    val fuelType: String?,
    @SerializedName("current_mileage")
    val currentMileage: Double?,
    @SerializedName("image_urls")
    val imageUrls: List<String>?
)

data class CreateVehicleRequest(
    val make: String,
    val model: String,
    val year: Int,
    @SerializedName("current_mileage")
    val currentMileage: Int,
    val color: String? = null,
    @SerializedName("fuel_type")
    val fuelType: String? = null,
    val transmission: String? = null,
    @SerializedName("vehicle_type")
    val vehicleType: String? = null
)

fun VehicleDto.toVehicle(): Vehicle {
    return Vehicle(
        id = id,
        make = make,
        model = model,
        year = year,
        color = color,
        vehicleType = vehicleType,
        transmission = transmission,
        fuelType = fuelType,
        currentMileage = currentMileage,
        imageUrls = imageUrls.orEmpty()
    )
}
