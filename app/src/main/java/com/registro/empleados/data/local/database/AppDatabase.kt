package com.registro.empleados.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.registro.empleados.data.local.dao.*
import com.registro.empleados.data.local.entity.*
import com.registro.empleados.data.local.converter.LocalDateConverter
import com.registro.empleados.data.local.converter.LocalDateTimeConverter
import com.registro.empleados.data.database.daos.DiaLaboralDao
import com.registro.empleados.data.database.entities.DiaLaboral

/**
 * Base de datos principal de la aplicación.
 */
@Database(
    entities = [
        EmpleadoEntity::class,
        RegistroAsistenciaEntity::class,
        DiaLaboral::class,
        HorasEmpleadoMesEntity::class,
        AusenciaEntity::class
    ],
    version = 15, // Corregir nombres y DNIs de todos los sectores
    exportSchema = true
)
@TypeConverters(
    LocalDateConverter::class,
    LocalDateTimeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun empleadoDao(): EmpleadoDao
    abstract fun registroAsistenciaDao(): RegistroAsistenciaDao
    abstract fun diaLaboralDao(): DiaLaboralDao
    abstract fun horasEmpleadoMesDao(): HorasEmpleadoMesDao
    abstract fun ausenciaDao(): AusenciaDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        // Migración de la versión 1 a la 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crear tabla temporal con nueva estructura
                database.execSQL("""
                    CREATE TABLE registros_asistencia_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        legajo_empleado TEXT NOT NULL,
                        fecha TEXT NOT NULL,
                        horas_trabajadas INTEGER NOT NULL,
                        observaciones TEXT,
                        fecha_registro INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(legajo_empleado) REFERENCES empleados(legajo) ON DELETE CASCADE
                    )
                """.trimIndent())

                // Copiar datos existentes (si hay) adaptándolos
                // Como no hay datos previos en la nueva estructura, simplemente creamos la tabla vacía
                // Los datos se insertarán cuando se use la nueva funcionalidad
                
                // Eliminar tabla vieja
                database.execSQL("DROP TABLE registros_asistencia")
                
                // Renombrar tabla nueva
                database.execSQL("ALTER TABLE registros_asistencia_new RENAME TO registros_asistencia")
                
                // Recrear índice
                database.execSQL("CREATE INDEX index_registros_asistencia_legajo_empleado ON registros_asistencia(legajo_empleado)")
            }
        }
        
        // Migración de la versión 2 a la 3
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Eliminar tabla existente si existe (para evitar conflictos de esquema)
                try {
                    database.execSQL("DROP TABLE IF EXISTS ausencia_table")
                } catch (e: Exception) {
                    // Tabla no existe, continuar
                }

                // Crear tabla de ausencias desde cero
                database.execSQL("""
                    CREATE TABLE ausencia_table (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        legajoEmpleado TEXT NOT NULL,
                        nombreEmpleado TEXT NOT NULL,
                        fechaInicio TEXT NOT NULL,
                        fechaFin TEXT NOT NULL,
                        motivo TEXT,
                        fechaCreacion TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }
        
        // Migración de la versión 3 a la 4
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Eliminar tabla de días laborales existente (esquema incorrecto)
                try {
                    database.execSQL("DROP TABLE IF EXISTS dias_laborales")
                } catch (e: Exception) {
                    // Tabla no existe, continuar
                }

                // Crear tabla de días laborales con el esquema correcto
                database.execSQL("""
                    CREATE TABLE dias_laborales (
                        fecha TEXT NOT NULL,
                        es_laboral INTEGER NOT NULL,
                        tipo_dia TEXT NOT NULL,
                        descripcion TEXT,
                        fecha_actualizacion INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(fecha)
                    )
                """.trimIndent())
            }
        }
        
        // Migración de la versión 4 a la 5
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crear tabla temporal con nueva estructura (nombreCompleto)
                database.execSQL("""
                    CREATE TABLE empleados_new (
                        legajo TEXT NOT NULL,
                        nombreCompleto TEXT NOT NULL,
                        sector TEXT NOT NULL,
                        fechaIngreso TEXT NOT NULL,
                        activo INTEGER NOT NULL,
                        fechaCreacion TEXT NOT NULL,
                        PRIMARY KEY(legajo)
                    )
                """.trimIndent())

                // Copiar datos existentes combinando nombre y apellido
                database.execSQL("""
                    INSERT INTO empleados_new (legajo, nombreCompleto, sector, fechaIngreso, activo, fechaCreacion)
                    SELECT legajo, 
                           CASE 
                               WHEN nombre IS NOT NULL AND apellido IS NOT NULL 
                               THEN nombre || ' ' || apellido
                               WHEN nombre IS NOT NULL 
                               THEN nombre
                               WHEN apellido IS NOT NULL 
                               THEN apellido
                               ELSE 'SIN NOMBRE'
                           END as nombreCompleto,
                           sector, 
                           fechaIngreso, 
                           activo, 
                           fechaCreacion
                    FROM empleados
                """.trimIndent())
                
                // Eliminar tabla vieja
                database.execSQL("DROP TABLE empleados")
                
                // Renombrar tabla nueva
                database.execSQL("ALTER TABLE empleados_new RENAME TO empleados")
            }
        }
        
        // Migración de la versión 7 a la 8 - Hacer legajo nullable
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Crear tabla temporal con nueva estructura
                database.execSQL("""
                    CREATE TABLE empleados_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        legajo TEXT,
                        nombreCompleto TEXT NOT NULL,
                        sector TEXT NOT NULL,
                        fechaIngreso TEXT NOT NULL,
                        activo INTEGER NOT NULL DEFAULT 1,
                        fechaCreacion TEXT NOT NULL
                    )
                """)
                
                // 2. Copiar datos existentes (legajo puede ser NULL ahora)
                database.execSQL("""
                    INSERT INTO empleados_new (id, legajo, nombreCompleto, sector, fechaIngreso, activo, fechaCreacion)
                    SELECT 
                        CAST(legajo AS INTEGER) as id,
                        CASE WHEN legajo LIKE 'AUTO_%' THEN NULL ELSE legajo END as legajo,
                        nombreCompleto,
                        sector,
                        fechaIngreso,
                        activo,
                        fechaCreacion
                    FROM empleados
                """)
                
                // 3. Eliminar tabla vieja
                database.execSQL("DROP TABLE empleados")
                
                // 4. Renombrar tabla nueva
                database.execSQL("ALTER TABLE empleados_new RENAME TO empleados")
                
                // 5. Crear índice para legajo (opcional, para búsquedas)
                database.execSQL("CREATE INDEX index_empleado_legajo ON empleados(legajo)")
            }
        }
        
        // Migración de la versión 8 a la 9 - Actualizar RegistroAsistencia para usar ID
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Crear tabla temporal con nueva estructura para registros
                database.execSQL("""
                    CREATE TABLE registros_asistencia_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        id_empleado INTEGER NOT NULL,
                        legajo_empleado TEXT,
                        fecha TEXT NOT NULL,
                        horas_trabajadas INTEGER NOT NULL,
                        observaciones TEXT,
                        fecha_registro INTEGER NOT NULL,
                        FOREIGN KEY(id_empleado) REFERENCES empleados(id) ON DELETE CASCADE
                    )
                """)
                
                // 2. Copiar datos existentes (si los hay)
                database.execSQL("""
                    INSERT INTO registros_asistencia_new (id, id_empleado, legajo_empleado, fecha, horas_trabajadas, observaciones, fecha_registro)
                    SELECT 
                        r.id,
                        e.id as id_empleado,
                        r.legajo_empleado,
                        r.fecha,
                        r.horas_trabajadas,
                        r.observaciones,
                        r.fecha_registro
                    FROM registros_asistencia r
                    JOIN empleados e ON e.legajo = r.legajo_empleado
                """)
                
                // 3. Eliminar tabla vieja
                database.execSQL("DROP TABLE registros_asistencia")
                
                // 4. Renombrar tabla nueva
                database.execSQL("ALTER TABLE registros_asistencia_new RENAME TO registros_asistencia")
                
                // 5. Crear índice para id_empleado
                database.execSQL("CREATE INDEX index_registros_asistencia_id_empleado ON registros_asistencia(id_empleado)")
                
                // 6. Actualizar tabla horas_empleado_mes
                database.execSQL("""
                    CREATE TABLE horas_empleado_mes_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        id_empleado INTEGER NOT NULL,
                        legajo_empleado TEXT,
                        año INTEGER NOT NULL,
                        mes INTEGER NOT NULL,
                        total_horas REAL NOT NULL,
                        dias_trabajados INTEGER NOT NULL,
                        promedio_diario REAL NOT NULL,
                        ultima_actualizacion TEXT NOT NULL,
                        FOREIGN KEY(id_empleado) REFERENCES empleados(id) ON DELETE CASCADE
                    )
                """)
                
                // 7. Copiar datos de horas_empleado_mes (si los hay)
                database.execSQL("""
                    INSERT INTO horas_empleado_mes_new (id, id_empleado, legajo_empleado, año, mes, total_horas, dias_trabajados, promedio_diario, ultima_actualizacion)
                    SELECT 
                        h.id,
                        e.id as id_empleado,
                        h.legajo_empleado,
                        h.año,
                        h.mes,
                        h.total_horas,
                        h.dias_trabajados,
                        h.promedio_diario,
                        h.ultima_actualizacion
                    FROM horas_empleado_mes h
                    JOIN empleados e ON e.legajo = h.legajo_empleado
                """)
                
                // 8. Eliminar tabla vieja de horas
                database.execSQL("DROP TABLE horas_empleado_mes")
                
                // 9. Renombrar tabla nueva de horas
                database.execSQL("ALTER TABLE horas_empleado_mes_new RENAME TO horas_empleado_mes")
                
                // 10. Crear índice para id_empleado en horas
                database.execSQL("CREATE INDEX index_horas_empleado_mes_id_empleado ON horas_empleado_mes(id_empleado)")
            }
        }
        
        // Migración de la versión 12 a la 13 - Agregar campo observacion a empleados
        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Agregar columna observacion a la tabla empleados
                database.execSQL("ALTER TABLE empleados ADD COLUMN observacion TEXT")
            }
        }
        
        // Migración de la versión 13 a la 14 - Reordenar nombres a formato APELLIDO NOMBRE
        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Actualizar nombreCompleto para invertir el orden de las palabras
                // De "NOMBRE APELLIDO" a "APELLIDO NOMBRE"
                database.execSQL("""
                    UPDATE empleados 
                    SET nombreCompleto = (
                        SELECT 
                            CASE 
                                WHEN INSTR(TRIM(nombreCompleto), ' ') > 0 THEN
                                    SUBSTR(TRIM(nombreCompleto), INSTR(TRIM(nombreCompleto), ' ') + 1) || ' ' || 
                                    SUBSTR(TRIM(nombreCompleto), 1, INSTR(TRIM(nombreCompleto), ' ') - 1)
                                ELSE 
                                    TRIM(nombreCompleto)
                            END
                    )
                    WHERE nombreCompleto IS NOT NULL
                """)
            }
        }
        
        // Migración de la versión 14 a la 15 - Corregir nombres y DNIs sector por sector
        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(database: SupportSQLiteDatabase) {
                android.util.Log.d("Migration_14_15", "🔧 === INICIANDO MIGRACIÓN 14→15 ===")
                
                // ===== SECTOR: RUTA 5 =====
                android.util.Log.d("Migration_14_15", "Corrigiendo RUTA 5...")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ARECO JUAN', legajo = NULL WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%ARECO%' AND nombreCompleto LIKE '%JUAN%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'BARROZO VICTOR', legajo = '40466618' WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%BARROZO%' AND nombreCompleto LIKE '%VICTOR%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'CABAÑA DIEGO', legajo = NULL WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%CABA%' AND nombreCompleto LIKE '%DIEGO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'COSTILLA ANTONIO', legajo = NULL WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%COSTILLA%' AND nombreCompleto LIKE '%ANTONIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'COSTILLA NERY', legajo = NULL WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%COSTILLA%' AND nombreCompleto LIKE '%NERY%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'DAVALOS HECTOR', legajo = NULL WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%DAVALOS%' AND nombreCompleto LIKE '%HECTOR%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'JUAREZ JULIO', legajo = '23359260' WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%JUAREZ%' AND nombreCompleto LIKE '%JULIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'LOPEZ JESUS', legajo = NULL WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%LOPEZ%' AND nombreCompleto LIKE '%JESUS%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'LOPEZ SERGIO', legajo = NULL WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%LOPEZ%' AND nombreCompleto LIKE '%SERGIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'RAMOS LEOCADIO', legajo = NULL WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%RAMOS%' AND nombreCompleto LIKE '%LEOCADIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'SANCHEZ HUGO', legajo = '36621974' WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%SANCHEZ%' AND nombreCompleto LIKE '%HUGO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'SOLOZA LUIS', legajo = NULL WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%SOLOZA%' AND nombreCompleto LIKE '%LUIS%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'VILLAGRAN PEDRO', legajo = NULL WHERE sector = 'RUTA 5' AND nombreCompleto LIKE '%VILLAGRAN%' AND nombreCompleto LIKE '%PEDRO%'")
                
                // ===== SECTOR: VIALSA =====
                database.execSQL("UPDATE empleados SET nombreCompleto = 'CASASOLA JORGE HECTOR', legajo = '2725285' WHERE sector = 'VIALSA' AND nombreCompleto LIKE '%CASASOLA%' AND nombreCompleto LIKE '%JORGE%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'CASASOLA WALTER MARINO', legajo = '26401152' WHERE sector = 'VIALSA' AND nombreCompleto LIKE '%CASASOLA%' AND nombreCompleto LIKE '%WALTER%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'VILLA HECTOR DANIEL', legajo = '35778402' WHERE sector = 'VIALSA' AND nombreCompleto LIKE '%VILLA%' AND nombreCompleto LIKE '%HECTOR%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'GARNICA EDUARDO', legajo = '32434296' WHERE sector = 'VIALSA' AND nombreCompleto LIKE '%GARNICA%' AND nombreCompleto LIKE '%EDUARDO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'PAZ MARTIN', legajo = '21933166' WHERE sector = 'VIALSA' AND nombreCompleto LIKE '%PAZ%' AND nombreCompleto LIKE '%MARTIN%'")
                
                // ===== SECTOR: CONSTRUCCION =====
                database.execSQL("UPDATE empleados SET nombreCompleto = 'CUELLAR FACUNDO', legajo = '38650700' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%CUELLAR%' AND nombreCompleto LIKE '%FACUNDO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'AHUMADA JAVIER', legajo = NULL WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%AHUMADA%' AND nombreCompleto LIKE '%JAVIER%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'BRIZUELA SANDRO', legajo = '22492209' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%BRIZUELA%' AND nombreCompleto LIKE '%SANDRO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'CESPEDES GONZALO', legajo = NULL WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%CESPEDES%' AND nombreCompleto LIKE '%GONZALO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'COHAN ANTONIO', legajo = NULL WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%COHAN%' AND nombreCompleto LIKE '%ANTONIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'CHILANGO ANDROSIO', legajo = NULL WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%CHILANGO%' AND nombreCompleto LIKE '%ANDROSIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'CUJUJUBA ROQUE', legajo = '16970546' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%CUJUJUBA%' AND nombreCompleto LIKE '%ROQUE%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ESPINOZA RODOLFO', legajo = '14916117' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%ESPINOZA%' AND nombreCompleto LIKE '%RODOLFO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'GUANTAY ALVARO', legajo = '38042465' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%GUANTAY%' AND nombreCompleto LIKE '%ALVARO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'GUANTAY FEDERICO', legajo = '36951679' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%GUANTAY%' AND nombreCompleto LIKE '%FEDERICO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'HUERTA NICOLAS', legajo = '39400694' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%HUERTA%' AND nombreCompleto LIKE '%NICOLAS%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'IBAÑEZ NANCY', legajo = '24898307' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%IBA%' AND nombreCompleto LIKE '%NANCY%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'MARTIN RUBEN', legajo = '23385514' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%MARTIN%' AND nombreCompleto LIKE '%RUBEN%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'MEDINA ADRIAN', legajo = NULL WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%MEDINA%' AND nombreCompleto LIKE '%ADRIAN%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'MOYA PEDRO', legajo = '16688040' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%MOYA%' AND nombreCompleto LIKE '%PEDRO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'NUÑEZ PABLO', legajo = '39217740' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%NU%' AND nombreCompleto LIKE '%PABLO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'PAZ EVELIO JESUS', legajo = '23680313' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%PAZ%' AND nombreCompleto LIKE '%EVELIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'OLIVERA BERTO', legajo = '31496973' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%OLIVERA%' AND nombreCompleto LIKE '%BERTO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'OSTRIANO JOAQUIN', legajo = '30190455' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%OSTRIANO%' AND nombreCompleto LIKE '%JOAQUIN%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'SALDNIA ALEXANDER', legajo = '39596641' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%SALDNIA%' AND nombreCompleto LIKE '%ALEXANDER%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'TERCEROS JAVIER', legajo = '26179539' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%TERCEROS%' AND nombreCompleto LIKE '%JAVIER%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'VEGA JULIAN', legajo = '27547361' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%VEGA%' AND nombreCompleto LIKE '%JULIAN%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'VELARDE RAMIRO', legajo = '38036002' WHERE sector = 'CONSTRUCCION' AND nombreCompleto LIKE '%VELARDE%' AND nombreCompleto LIKE '%RAMIRO%'")
                
                // ===== SECTOR: CUCHUY =====
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ANGEL JAVIER', legajo = NULL WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%ANGEL%' AND nombreCompleto LIKE '%JAVIER%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ARAO MARCELO ALEJANDRO', legajo = NULL WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%ARAO%' AND nombreCompleto LIKE '%MARCELO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'AYLAN EZEQUIEL', legajo = NULL WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%AYLAN%' AND nombreCompleto LIKE '%EZEQUIEL%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'BRANDAN PEDRO', legajo = '39364061' WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%BRANDAN%' AND nombreCompleto LIKE '%PEDRO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'FERNANDEZ ADRIAN', legajo = NULL WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%FERNANDEZ%' AND nombreCompleto LIKE '%ADRIAN%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'GONGORA LUIS', legajo = '37538315' WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%GONGORA%' AND nombreCompleto LIKE '%LUIS%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'LLAMANI MAXIMILIANO', legajo = NULL WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%LLAMANI%' AND nombreCompleto LIKE '%MAXIMILIANO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'MOLINA CLAUDIO', legajo = NULL WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%MOLINA%' AND nombreCompleto LIKE '%CLAUDIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'PONCE JORGE ADRIAN', legajo = NULL WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%PONCE%' AND nombreCompleto LIKE '%JORGE%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'RAMOS ALEJANDRO', legajo = '32292665' WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%RAMOS%' AND nombreCompleto LIKE '%ALEJANDRO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ROJAS MARIO', legajo = '42488208' WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%ROJAS%' AND nombreCompleto LIKE '%MARIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'SORIA EMANUEL', legajo = '40966366' WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%SORIA%' AND nombreCompleto LIKE '%EMANUEL%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'TEVES ESTEBAN', legajo = '44566024' WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%TEVES%' AND nombreCompleto LIKE '%ESTEBAN%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'TEVEZ FERNANDO', legajo = '42488275' WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%TEVEZ%' AND nombreCompleto LIKE '%FERNANDO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'TORRES GERARDO', legajo = '44910813' WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%TORRES%' AND nombreCompleto LIKE '%GERARDO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'TORRES HECTOR', legajo = '40150119' WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%TORRES%' AND nombreCompleto LIKE '%HECTOR%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'RAMOS JOSE', legajo = '32292665' WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%RAMOS%' AND nombreCompleto LIKE '%JOSE%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'JUAREZ GUSTAVO', legajo = '30675083' WHERE sector = 'CUCHUY' AND nombreCompleto LIKE '%JUAREZ%' AND nombreCompleto LIKE '%GUSTAVO%'")
                
                // ===== SECTOR: MOSCONI =====
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ARANDA OLGA', legajo = '20877325' WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%ARANDA%' AND nombreCompleto LIKE '%OLGA%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ACOSTA MATIAS', legajo = '18892104' WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%ACOSTA%' AND nombreCompleto LIKE '%MATIAS%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'CHIPIPI OCTAVIO', legajo = '45116276' WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%CHIPIPI%' AND nombreCompleto LIKE '%OCTAVIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ROBLE LUIS', legajo = '32893473' WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%ROBLE%' AND nombreCompleto LIKE '%LUIS%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'REINALDO BENJAMIN', legajo = NULL WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%REINALDO%' AND nombreCompleto LIKE '%BENJAMIN%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'SEGUNDO JUAN BERN', legajo = '31346481' WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%SEGUNDO%' AND nombreCompleto LIKE '%JUAN%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'SEGUNDO MARIANO', legajo = NULL WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%SEGUNDO%' AND nombreCompleto LIKE '%MARIANO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'SABINO', legajo = NULL WHERE sector = 'MOSCONI' AND nombreCompleto = 'SABINO'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'SEGUNDO CARLOS', legajo = NULL WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%SEGUNDO%' AND nombreCompleto LIKE '%CARLOS%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ALBERTO', legajo = '39784691' WHERE sector = 'MOSCONI' AND nombreCompleto = 'ALBERTO'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'SEGUNDO ELIAS', legajo = '40329769' WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%SEGUNDO%' AND nombreCompleto LIKE '%ELIAS%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'MARTINEZ WILFREDO', legajo = '40326244' WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%MARTINEZ%' AND nombreCompleto LIKE '%WILFREDO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'LUCIANO BALDINO', legajo = NULL WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%LUCIANO%' AND nombreCompleto LIKE '%BALDINO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ROJAS MAXIMILIANO ANT', legajo = NULL WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%ROJAS%' AND nombreCompleto LIKE '%MAXIMILIANO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'FLORES FABIO', legajo = '19037217' WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%FLORES%' AND nombreCompleto LIKE '%FABIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ARANDA GABRIEL', legajo = '35782469' WHERE sector = 'MOSCONI' AND nombreCompleto LIKE '%ARANDA%' AND nombreCompleto LIKE '%GABRIEL%'")
                
                // ===== SECTOR: PICADO Y COSECHA =====
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ARIAS EULOGIO ANTONIO', legajo = '41734306' WHERE sector = 'PICADO Y COSECHA' AND nombreCompleto LIKE '%ARIAS%' AND nombreCompleto LIKE '%EULOGIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ABAN SERGIO', legajo = '27522020' WHERE sector = 'PICADO Y COSECHA' AND nombreCompleto LIKE '%ABAN%' AND nombreCompleto LIKE '%SERGIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ALDERETE IVAN', legajo = '42897722' WHERE sector = 'PICADO Y COSECHA' AND nombreCompleto LIKE '%ALDERETE%' AND nombreCompleto LIKE '%IVAN%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'ANACHURI JOSE', legajo = '33056047' WHERE sector = 'PICADO Y COSECHA' AND nombreCompleto LIKE '%ANACHURI%' AND nombreCompleto LIKE '%JOSE%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'CAMPOS ISIDRO', legajo = '18737938' WHERE sector = 'PICADO Y COSECHA' AND nombreCompleto LIKE '%CAMPOS%' AND nombreCompleto LIKE '%ISIDRO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'CRUZ DANILO', legajo = '27169429' WHERE sector = 'PICADO Y COSECHA' AND nombreCompleto LIKE '%CRUZ%' AND nombreCompleto LIKE '%DANILO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'MONTES VICTOR', legajo = '24169748' WHERE sector = 'PICADO Y COSECHA' AND nombreCompleto LIKE '%MONTES%' AND nombreCompleto LIKE '%VICTOR%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'TEJERINA CLAUDIO FERNA', legajo = '32434281' WHERE sector = 'PICADO Y COSECHA' AND nombreCompleto LIKE '%TEJERINA%' AND nombreCompleto LIKE '%CLAUDIO%'")
                database.execSQL("UPDATE empleados SET nombreCompleto = 'RODRIGUEZ JUAN CARLOS', legajo = '37420050' WHERE sector = 'PICADO Y COSECHA' AND nombreCompleto LIKE '%RODRIGUEZ%' AND nombreCompleto LIKE '%JUAN%'")
                
                // ===== LIMPIAR TODOS LOS LEGAJOS AUTO_* =====
                android.util.Log.d("Migration_14_15", "Limpiando legajos AUTO_*...")
                database.execSQL("UPDATE empleados SET legajo = NULL WHERE legajo LIKE 'AUTO_%'")
                
                android.util.Log.d("Migration_14_15", "✅ === MIGRACIÓN 14→15 COMPLETADA ===")
            }
        }
        
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "registro_empleados_database"
                )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_7_8,
                    MIGRATION_8_9,
                    MIGRATION_12_13,
                    MIGRATION_13_14,
                    MIGRATION_14_15
                )
                .fallbackToDestructiveMigration()  // Para desarrollo - borra y recrea
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
