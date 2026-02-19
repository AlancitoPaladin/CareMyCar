package com.itsm.caremycar.repository

import com.itsm.caremycar.api.ApiService
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.CatalogVehicle
import com.itsm.caremycar.vehicle.CreateMaintenanceRequest
import com.itsm.caremycar.vehicle.CreateVehicleRequest
import com.itsm.caremycar.vehicle.MaintenanceRecord
import com.itsm.caremycar.vehicle.toMaintenanceRecord
import com.itsm.caremycar.vehicle.Vehicle
import com.itsm.caremycar.vehicle.toCatalogVehicle
import com.itsm.caremycar.vehicle.toVehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun updateMaintenance(
        maintenanceId: String,
        payload: Map<String, Any>
    ): Resource<MaintenanceRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateMaintenance(maintenanceId, payload)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.maintenance.toMaintenanceRecord())
                } else {
                    Resource.Error(
                        parseBackendError(response.errorBody()?.string())
                            ?: "No se pudo actualizar el mantenimiento."
                    )
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    suspend fun deleteMaintenance(maintenanceId: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteMaintenance(maintenanceId)
                if (response.isSuccessful) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(
                        parseBackendError(response.errorBody()?.string())
                            ?: "No se pudo eliminar el mantenimiento."
                    )
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    suspend fun listMaintenanceByVehicle(vehicleId: String): Resource<List<MaintenanceRecord>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listMaintenanceByVehicle(vehicleId)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.items.map { it.toMaintenanceRecord() })
                } else {
                    Resource.Error(
                        parseBackendError(response.errorBody()?.string())
                            ?: "No se pudo cargar el mantenimiento."
                    )
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    suspend fun createMaintenance(request: CreateMaintenanceRequest): Resource<MaintenanceRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createMaintenance(request)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.maintenance.toMaintenanceRecord())
                } else {
                    Resource.Error(
                        parseBackendError(response.errorBody()?.string())
                            ?: "No se pudo crear el registro de mantenimiento."
                    )
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    suspend fun listCatalogVehicles(): Resource<List<CatalogVehicle>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listCatalogVehicles()
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.items.map { it.toCatalogVehicle() })
                } else {
                    Resource.Error("No se pudo cargar el catálogo de vehículos.")
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    suspend fun updateVehicle(
        vehicleId: String,
        payload: Map<String, Any>
    ): Resource<Vehicle> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateVehicle(vehicleId, payload)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.vehicle.toVehicle())
                } else {
                    Resource.Error(
                        parseBackendError(response.errorBody()?.string())
                            ?: when (response.code()) {
                                400 -> "Datos inválidos para actualizar vehículo."
                                404 -> "Vehículo no encontrado o sin cambios."
                                else -> "No se pudo actualizar el vehículo."
                            }
                    )
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    suspend fun deleteVehicle(vehicleId: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteVehicle(vehicleId)
                if (response.isSuccessful) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(
                        parseBackendError(response.errorBody()?.string())
                            ?: when (response.code()) {
                                400 -> "ID de vehículo inválido."
                                404 -> "Vehículo no encontrado."
                                401 -> "Sesión expirada. Inicia sesión de nuevo."
                                else -> "No se pudo eliminar el vehículo."
                            }
                    )
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    suspend fun createVehicle(request: CreateVehicleRequest): Resource<Vehicle> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createVehicle(request)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.vehicle.toVehicle())
                } else {
                    Resource.Error(
                        parseBackendError(response.errorBody()?.string())
                            ?: when (response.code()) {
                                400 -> "Datos de vehiculo invalidos."
                                401 -> "Sesion expirada. Inicia sesion de nuevo."
                                else -> "No se pudo crear el vehiculo."
                            }
                    )
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexion")
            }
        }
    }

    suspend fun listVehicles(): Resource<List<Vehicle>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listVehicles()
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.items.map { it.toVehicle() })
                } else {
                    Resource.Error(
                        when (response.code()) {
                            401 -> "Sesión expirada. Inicia sesión de nuevo."
                            else -> "No se pudieron cargar tus vehículos."
                        }
                    )
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    suspend fun getVehicleById(vehicleId: String): Resource<Vehicle> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getVehicleById(vehicleId)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.vehicle.toVehicle())
                } else {
                    Resource.Error(
                        when (response.code()) {
                            404 -> "Vehículo no encontrado."
                            else -> "No se pudo cargar el detalle del vehículo."
                        }
                    )
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    private fun parseBackendError(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        val singleError = Regex("\"error\"\\s*:\\s*\"([^\"]+)\"").find(errorBody)?.groupValues?.getOrNull(1)
        if (!singleError.isNullOrBlank()) return singleError
        val errorsArray = Regex("\"errors\"\\s*:\\s*\\[(.*?)]").find(errorBody)?.groupValues?.getOrNull(1)
        if (!errorsArray.isNullOrBlank()) {
            return errorsArray
                .split(",")
                .map { it.trim().trim('"') }
                .filter { it.isNotBlank() }
                .joinToString("\n")
                .ifBlank { null }
        }
        return null
    }
}
