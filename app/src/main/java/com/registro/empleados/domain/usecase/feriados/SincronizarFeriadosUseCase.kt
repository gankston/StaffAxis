package com.registro.empleados.domain.usecase.feriados

import com.registro.empleados.data.remote.datasource.FeriadosRemoteDataSource
import com.registro.empleados.domain.repository.DiaLaboralRepository
import javax.inject.Inject

/**
 * Caso de uso para sincronizar feriados argentinos desde la API externa.
 * Descarga los feriados y los actualiza en la base de datos local.
 */
class SincronizarFeriadosUseCase @Inject constructor(
    private val feriadosRemoteDataSource: FeriadosRemoteDataSource,
    private val diaLaboralRepository: DiaLaboralRepository
) {
    
    /**
     * Resultado de la sincronización de feriados.
     */
    data class SincronizacionResult(
        val exito: Boolean,
        val feriadosSincronizados: Int,
        val mensaje: String
    )
    
    /**
     * Sincroniza los feriados para un año específico.
     * @param año Año para sincronizar los feriados
     * @return Resultado de la sincronización
     */
    suspend operator fun invoke(año: Int): SincronizacionResult {
        return try {
            // Obtener feriados desde la API
            val feriados = feriadosRemoteDataSource.getFeriados(año)
            
            // Verificar si hay feriados para sincronizar
            if (feriados.isEmpty()) {
                return SincronizacionResult(
                    exito = false,
                    feriadosSincronizados = 0,
                    mensaje = "No se encontraron feriados para el año $año"
                )
            }
            
            // Insertar o actualizar feriados en la base de datos local
            diaLaboralRepository.insertDias(feriados)
            
            SincronizacionResult(
                exito = true,
                feriadosSincronizados = feriados.size,
                mensaje = "Se sincronizaron ${feriados.size} feriados para el año $año"
            )
        } catch (e: Exception) {
            SincronizacionResult(
                exito = false,
                feriadosSincronizados = 0,
                mensaje = "Error al sincronizar feriados: ${e.message}"
            )
        }
    }
    
    /**
     * Sincroniza los feriados para el año actual.
     * @return Resultado de la sincronización
     */
    suspend fun sincronizarFeriadosActuales(): SincronizacionResult {
        val añoActual = java.time.LocalDate.now().year
        return invoke(añoActual)
    }
    
    /**
     * Sincroniza los feriados para múltiples años.
     * Útil para inicializar datos históricos o futuros.
     * @param años Lista de años para sincronizar
     * @return Lista de resultados de sincronización por año
     */
    suspend fun sincronizarFeriadosMultiplesAños(años: List<Int>): List<SincronizacionResult> {
        val resultados = mutableListOf<SincronizacionResult>()
        
        años.forEach { año ->
            val resultado = invoke(año)
            resultados.add(resultado)
        }
        
        return resultados
    }
    
    /**
     * Verifica si la API de feriados está disponible.
     * @return true si la API responde correctamente, false en caso contrario
     */
    suspend fun verificarDisponibilidadApi(): Boolean {
        return try {
            feriadosRemoteDataSource.isApiAvailable()
        } catch (e: Exception) {
            false
        }
    }
}
