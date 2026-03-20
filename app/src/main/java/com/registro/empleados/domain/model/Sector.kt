package com.registro.empleados.domain.model

data class Sector(
    val id: String,
    val name: String,
    val encargado: String? = null
)

