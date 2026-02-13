package com.registro.empleados.data.remote.datasource

import com.registro.empleados.data.remote.api.FeriadosApiService
import com.registro.empleados.data.remote.dto.FeriadosResponseDto
import com.registro.empleados.domain.model.DiaLaboral
import com.registro.empleados.data.remote.mapper.FeriadoMapper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fuente de datos remota para feriados argentinos.
 * Maneja la comunicación con la API externa.
 */
@Singleton
class FeriadosRemoteDataSource @Inject constructor(
    private val feriadosApiService: FeriadosApiService
) {
    
    /**
     * Obtiene los feriados nacionales para un año específico.
     * @param año Año para consultar los feriados
     * @return Lista de días laborales representando los feriados
     * @throws Exception Si hay error en la comunicación con la API
     */
    suspend fun getFeriados(año: Int): List<DiaLaboral> {
        try {
            val response = feriadosApiService.getFeriados(año)
            
            if (response.isSuccessful) {
                val feriadosResponse = response.body()
                if (feriadosResponse != null && feriadosResponse.isNotEmpty()) {
                    // Extraer todos los feriados de la respuesta
                    val todosLosFeriados = mutableListOf<com.registro.empleados.data.remote.dto.FeriadoDto>()
                    
                    feriadosResponse.forEach { responseItem ->
                        // Agregar el feriado principal si existe
                        if (responseItem.dia != null && responseItem.mes != null) {
                            val feriadoPrincipal = com.registro.empleados.data.remote.dto.FeriadoDto(
                                motivo = responseItem.motivo ?: "Feriado Nacional",
                                tipo = responseItem.tipo ?: "inamovible",
                                info = responseItem.info ?: "",
                                dia = responseItem.dia,
                                mes = responseItem.mes,
                                id = responseItem.id ?: ""
                            )
                            todosLosFeriados.add(feriadoPrincipal)
                        }
                        
                        // Agregar feriados adicionales si existen
                        todosLosFeriados.addAll(responseItem.feriados)
                    }
                    
                    return FeriadoMapper.toDiaLaboralList(todosLosFeriados, año)
                } else {
                    throw Exception("No se encontraron feriados para el año $año")
                }
            } else {
                throw Exception("Error al obtener feriados: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Error en la comunicación con la API de feriados: ${e.message}")
        }
    }
    
    /**
     * Obtiene los feriados nacionales para el año actual.
     * @return Lista de días laborales representando los feriados
     * @throws Exception Si hay error en la comunicación con la API
     */
    suspend fun getFeriadosActuales(): List<DiaLaboral> {
        try {
            val response = feriadosApiService.getFeriadosActuales()
            
            if (response.isSuccessful) {
                val feriadosResponse = response.body()
                if (feriadosResponse != null && feriadosResponse.isNotEmpty()) {
                    val añoActual = java.time.LocalDate.now().year
                    
                    // Extraer todos los feriados de la respuesta
                    val todosLosFeriados = mutableListOf<com.registro.empleados.data.remote.dto.FeriadoDto>()
                    
                    feriadosResponse.forEach { responseItem ->
                        // Agregar el feriado principal si existe
                        if (responseItem.dia != null && responseItem.mes != null) {
                            val feriadoPrincipal = com.registro.empleados.data.remote.dto.FeriadoDto(
                                motivo = responseItem.motivo ?: "Feriado Nacional",
                                tipo = responseItem.tipo ?: "inamovible",
                                info = responseItem.info ?: "",
                                dia = responseItem.dia,
                                mes = responseItem.mes,
                                id = responseItem.id ?: ""
                            )
                            todosLosFeriados.add(feriadoPrincipal)
                        }
                        
                        // Agregar feriados adicionales si existen
                        todosLosFeriados.addAll(responseItem.feriados)
                    }
                    
                    return FeriadoMapper.toDiaLaboralList(todosLosFeriados, añoActual)
                } else {
                    throw Exception("No se encontraron feriados para el año actual")
                }
            } else {
                throw Exception("Error al obtener feriados actuales: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Error en la comunicación con la API de feriados: ${e.message}")
        }
    }
    
    /**
     * Verifica si la API está disponible.
     * @return true si la API responde correctamente, false en caso contrario
     */
    suspend fun isApiAvailable(): Boolean {
        return try {
            val response = feriadosApiService.getFeriadosActuales()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
