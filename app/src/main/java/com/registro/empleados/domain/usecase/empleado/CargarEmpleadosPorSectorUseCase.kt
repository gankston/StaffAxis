package com.registro.empleados.domain.usecase.empleado

import com.registro.empleados.domain.repository.EmpleadoRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para cargar empleados predefinidos por sector.
 * Inserta todos los empleados de los 6 sectores en la base de datos.
 */
class CargarEmpleadosPorSectorUseCase @Inject constructor(
    private val empleadoRepository: EmpleadoRepository
) {
    
    /**
     * Ejecuta el caso de uso para cargar todos los empleados por sector.
     * @return Resultado de la operación con estadísticas
     */
    suspend operator fun invoke(): String {
        val fechaIngreso = LocalDate.of(2025, 9, 1)
        var totalEmpleados = 0
        
        try {
            // SECTOR RUTA 5
            val empleadosRuta5 = listOf(
                EmpleadoData("2298", "JUAREZ", "JULIO", "RUTA 5", fechaIngreso),
                EmpleadoData("2366", "SANCHEZ", "HUGO", "RUTA 5", fechaIngreso),
                EmpleadoData("2367", "ARECO", "JUAN", "RUTA 5", fechaIngreso),
                EmpleadoData("2368", "BARROZO", "VICTOR", "RUTA 5", fechaIngreso),
                EmpleadoData("2369", "SOLOZA", "LUIS", "RUTA 5", fechaIngreso),
                EmpleadoData("2370", "LOPEZ", "SERGIO", "RUTA 5", fechaIngreso),
                EmpleadoData("2371", "VILLAGRAN", "PEDRO", "RUTA 5", fechaIngreso),
                EmpleadoData("2372", "SANTILLAN", "ANTONIO", "RUTA 5", fechaIngreso),
                EmpleadoData("2373", "LOPEZ", "JESUS", "RUTA 5", fechaIngreso),
                EmpleadoData("2374", "DAVALOS", "HECTOR", "RUTA 5", fechaIngreso),
                EmpleadoData("2375", "RAMOS", "LEOCADIO", "RUTA 5", fechaIngreso),
                EmpleadoData("2376", "COSTILLA", "NERY", "RUTA 5", fechaIngreso),
                EmpleadoData("2377", "COSTILLA", "ANTONIO", "RUTA 5", fechaIngreso),
                EmpleadoData("2378", "CABANA", "DIEGO", "RUTA 5", fechaIngreso)
            )
            
            // SECTOR VIALSA
            val empleadosVialsa = listOf(
                EmpleadoData("2119", "CASASOLA", "JORGE HECTOR", "VIALSA", fechaIngreso),
                EmpleadoData("2124", "CASASOLA", "WALTER MARINO", "VIALSA", fechaIngreso),
                EmpleadoData("2451", "VILLA", "HECTOR DANIEL", "VIALSA", fechaIngreso),
                EmpleadoData("3858", "GARNICA", "EDUARDO", "VIALSA", fechaIngreso),
                EmpleadoData("4769", "PAZ", "MARTIN", "VIALSA", fechaIngreso),
                EmpleadoData("AUTO_BURGOS", "BURGOS", "MARIANO", "VIALSA", fechaIngreso) // Legajo generado
            )
            
            // SECTOR MOSCONI
            val empleadosMosconi = listOf(
                EmpleadoData("2019", "ARANDA", "OLGA", "MOSCONI", fechaIngreso),
                EmpleadoData("2398", "ACOSTA", "MATIAS", "MOSCONI", fechaIngreso),
                EmpleadoData("AUTO_CHIPIPI", "CHIPIPI", "OCTAVIO", "MOSCONI", fechaIngreso),
                EmpleadoData("4312", "ROBLE", "LUIS", "MOSCONI", fechaIngreso),
                EmpleadoData("AUTO_REINALDO", "REINALDO", "BENAJMIN", "MOSCONI", fechaIngreso),
                EmpleadoData("5135", "SEGUNDO", "JUAN BERN", "MOSCONI", fechaIngreso),
                EmpleadoData("AUTO_SEGUNDO_MAR", "SEGUNDO", "MARIANO", "MOSCONI", fechaIngreso),
                EmpleadoData("AUTO_SABINO", "SABINO", "", "MOSCONI", fechaIngreso),
                EmpleadoData("AUTO_SEGUNDO_CAR", "SEGUNDO", "CARLOS", "MOSCONI", fechaIngreso),
                EmpleadoData("5136", "ALBERTO", "", "MOSCONI", fechaIngreso),
                EmpleadoData("5137", "SEGUNDO", "ELIAS", "MOSCONI", fechaIngreso),
                EmpleadoData("4164", "MARTINEZ", "WILFREDO", "MOSCONI", fechaIngreso),
                EmpleadoData("AUTO_LUCIANO", "LUCIANO", "BALDINO", "MOSCONI", fechaIngreso),
                EmpleadoData("AUTO_ROJAS", "ROJAS", "MAXIMILIANO ANT", "MOSCONI", fechaIngreso),
                EmpleadoData("5110", "FLORES", "FABIO", "MOSCONI", fechaIngreso),
                EmpleadoData("4471", "ARANDA", "GABRIEL", "MOSCONI", fechaIngreso)
            )
            
            // SECTOR CONSTRUCCION
            val empleadosConstruccion = listOf(
                EmpleadoData("AUTO_CUELLAR", "CUELLAR", "FACUNDO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2430", "BRIZUELA", "SANDRO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("AUTO_CESPEDES", "CESPEDES", "GONZALO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("AUTO_COMAN", "COMAN", "ANTONIO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("AUTO_CHILANGO", "CHILANGO", "AMBROSIO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2348", "CORDOBA", "ROQUE", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2336", "ESPINOZA", "RODOLFO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2341", "GUANTAY", "ALVARO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2331", "GUANTAY", "FEDERICO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2429", "HUERTA", "NICOLAS", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("3548", "IBAÑEZ", "NANCY", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2324", "MARTIN", "RUBEN", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("AUTO_MEDINA", "MEDINA", "ADRIAN", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2337", "MOYA", "PEDRO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2332", "NUÑEZ", "PABLO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("3807", "PAZ", "EVELIO JESUS", "CONSTRUCCION", fechaIngreso, "PAMPA"),
                EmpleadoData("2456", "OLIVERA", "BERTO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2441", "OSTRIANO", "JOAQUIN", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2442", "SALDRIA", "ALEXANDER", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2325", "TERCEROS", "JAVIER", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("2427", "VACA", "JULIAN", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("AUTO_VELARDE", "VELARDE", "RAMIRO", "CONSTRUCCION", fechaIngreso),
                EmpleadoData("5207", "ANAQUIN", "FRANCISCO", "CONSTRUCCION", fechaIngreso, "PAMPA"),
                EmpleadoData("AUTO_CEJAS", "CEJAS", "JUAN", "CONSTRUCCION", fechaIngreso, "PAMPA"),
                EmpleadoData("AUTO_LOPEZ", "LOPEZ", "JOSE", "CONSTRUCCION", fechaIngreso, "PAMPA"),
                EmpleadoData("AUTO_FIGUEROA", "FIGUEROA", "RAMIRO", "CONSTRUCCION", fechaIngreso, "PAMPA"),
                EmpleadoData("AUTO_MORENO", "MORENO", "JESUS", "CONSTRUCCION", fechaIngreso, "PAMPA"),
                EmpleadoData("5210", "OCAMPO", "ANDRES", "CONSTRUCCION", fechaIngreso, "PAMPA"),
                EmpleadoData("5211", "TOLABA", "ANDRES", "CONSTRUCCION", fechaIngreso, "PAMPA"),
                EmpleadoData("AUTO_TORO", "TORO", "ENRIQUE", "CONSTRUCCION", fechaIngreso, "PAMPA")
            )
            
            // SECTOR CUCHUY
            val empleadosCuchuy = listOf(
                EmpleadoData("2069", "FERNANDEZ", "EUSTACIO", "CUCHUY", fechaIngreso),
                EmpleadoData("2632", "TORRES", "HECTOR", "CUCHUY", fechaIngreso),
                EmpleadoData("5140", "TORRES", "GERARDO", "CUCHUY", fechaIngreso),
                EmpleadoData("10001", "GONGORA", "LUIS", "CUCHUY", fechaIngreso),
                EmpleadoData("5192", "ROJAS", "MARIO", "CUCHUY", fechaIngreso),
                EmpleadoData("5129", "TEVES", "ESTEBAN", "CUCHUY", fechaIngreso),
                EmpleadoData("5128", "TEVEZ", "FERNANDO", "CUCHUY", fechaIngreso),
                EmpleadoData("AUTO_SORIA", "SORIA", "EMANUEL", "CUCHUY", fechaIngreso),
                EmpleadoData("AUTO_BRANDAN", "BRANDAN", "PEDRO", "CUCHUY", fechaIngreso),
                EmpleadoData("AUTO_MOLINA", "MOLINA", "CLAUDIO", "CUCHUY", fechaIngreso),
                EmpleadoData("AUTO_FERNANDEZ_AD", "FERNANDEZ", "ADRIAN", "CUCHUY", fechaIngreso),
                EmpleadoData("AUTO_LLAMANI", "LLAMANI", "MAXIMILIANO", "CUCHUY", fechaIngreso),
                EmpleadoData("AUTO_ANGEL", "ANGEL", "JAVIER", "CUCHUY", fechaIngreso),
                EmpleadoData("AUTO_RAMOS", "RAMOS", "ALEJANDRO", "CUCHUY", fechaIngreso),
                EmpleadoData("AUTO_AYLAN", "AYLAN", "EZEQUIEL", "CUCHUY", fechaIngreso),
                EmpleadoData("AUTO_PONCE", "PONCE", "JORGE ADRIAN", "CUCHUY", fechaIngreso),
                EmpleadoData("AUTO_ALEJANDRO", "ALEJANDRO", "", "CUCHUY", fechaIngreso, "Solo nombre, sin apellido ni legajo")
            )
            
            // SECTOR PICADO Y COSECHA
            val empleadosPicadoCosecha = listOf(
                EmpleadoData("3964", "ARIAS", "EULOGIO ANTONIO", "PICADO Y COSECHA", fechaIngreso),
                EmpleadoData("4929", "ABAN", "SERGIO", "PICADO Y COSECHA", fechaIngreso),
                EmpleadoData("4228", "ALDERETE", "IVAN", "PICADO Y COSECHA", fechaIngreso),
                EmpleadoData("2500", "ANACHURI", "JOSE", "PICADO Y COSECHA", fechaIngreso),
                EmpleadoData("2072", "CAMPOS", "ISIDRO", "PICADO Y COSECHA", fechaIngreso, "TRILLADORA"),
                EmpleadoData("4612", "CRUZ", "DANILO", "PICADO Y COSECHA", fechaIngreso, "TRILLADORA"),
                EmpleadoData("2722", "MONTES", "VICTOR", "PICADO Y COSECHA", fechaIngreso, "TRILLADORA"),
                EmpleadoData("2383", "TEJERINA", "CLAUDIO FERNA", "PICADO Y COSECHA", fechaIngreso, "TRILLADORA"),
                EmpleadoData("2370", "RODRIGUEZ", "JUAN CARLOS", "PICADO Y COSECHA", fechaIngreso)
            )
            
            // Insertar todos los empleados
            val todosLosEmpleados = empleadosRuta5 + empleadosVialsa + empleadosMosconi + 
                                   empleadosConstruccion + empleadosCuchuy + empleadosPicadoCosecha
            
            for (empleadoData in todosLosEmpleados) {
                try {
                    empleadoRepository.insertEmpleado(
                        com.registro.empleados.domain.model.Empleado(
                            legajo = empleadoData.legajo,
                            nombreCompleto = "${empleadoData.apellido} ${empleadoData.nombre}".trim(),
                            sector = empleadoData.sector,
                            fechaIngreso = empleadoData.fechaIngreso,
                            activo = true,
                            fechaCreacion = LocalDate.now(),
                            observaciones = empleadoData.observaciones
                        )
                    )
                    totalEmpleados++
                } catch (e: Exception) {
                    android.util.Log.w("CargarEmpleadosPorSector", "Error insertando empleado ${empleadoData.legajo}: ${e.message}")
                }
            }
            
            return "✅ Empleados cargados exitosamente: $totalEmpleados empleados en 6 sectores"
            
        } catch (e: Exception) {
            android.util.Log.e("CargarEmpleadosPorSector", "Error cargando empleados por sector", e)
            throw e
        }
    }
    
    /**
     * Clase de datos para representar un empleado.
     */
    private data class EmpleadoData(
        val legajo: String,
        val nombre: String,
        val apellido: String,
        val sector: String,
        val fechaIngreso: LocalDate,
        val observaciones: String? = null
    )
}
