package com.registro.empleados.domain.model

/**
 * Representa un encargado y su sector asociado.
 * Cada encargado está ligado directamente a un sector.
 * Para agregar más encargados en el futuro, añadir entradas a ENCARGADOS_SECTORES.
 */
data class EncargadoSector(
    val nombreEncargado: String,
    val sector: String
)

/**
 * Lista de encargados con sus sectores.
 * A futuro se pueden agregar más encargados aquí.
 */
object EncargadosDisponibles {
    val ENCARGADOS_SECTORES: List<EncargadoSector> = listOf(
        EncargadoSector("HECTOR BARROZO", "RUTA 5"),
        EncargadoSector("DIEGO BERNARD", "CUCHUY"),
        EncargadoSector("SERGIO GODOY", "CONSTRUCCION"),
        EncargadoSector("MIGUEL MAURINO", "MOSCONI"),
        EncargadoSector("MAXI RUBIN", "PICADO Y COSECHA"),
        EncargadoSector("Sergio de Barbara", "VIALSA")
    )
}
