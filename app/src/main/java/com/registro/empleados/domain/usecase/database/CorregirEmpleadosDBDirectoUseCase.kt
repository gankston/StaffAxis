package com.registro.empleados.domain.usecase.database

import android.util.Log
import com.registro.empleados.data.local.dao.EmpleadoDao
import javax.inject.Inject

/**
 * UseCase para corregir empleados DIRECTAMENTE en la DB con datos específicos
 */
class CorregirEmpleadosDBDirectoUseCase @Inject constructor(
    private val empleadoDao: EmpleadoDao
) {
    
    suspend operator fun invoke() {
        try {
            Log.d("CorregirDBDirecto", "🔧 === CORRIGIENDO EMPLEADOS DIRECTAMENTE EN DB ===")
            
            // LIMPIAR TODOS LOS AUTO_*
            Log.d("CorregirDBDirecto", "Limpiando AUTO_*...")
            empleadoDao.limpiarLegajosAutomaticos()
            
            // ===== RUTA 5 =====
            Log.d("CorregirDBDirecto", "Corrigiendo RUTA 5...")
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "ARECO", "JUAN", "ARECO JUAN", null)
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "BARROZO", "VICTOR", "BARROZO VICTOR", "40466618")
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "CABA", "DIEGO", "CABAÑA DIEGO", null)
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "COSTILLA", "ANTONIO", "COSTILLA ANTONIO", null)
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "COSTILLA", "NERY", "COSTILLA NERY", null)
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "DAVALOS", "HECTOR", "DAVALOS HECTOR", null)
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "JUAREZ", "JULIO", "JUAREZ JULIO", "23359260")
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "LOPEZ", "JESUS", "LOPEZ JESUS", null)
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "LOPEZ", "SERGIO", "LOPEZ SERGIO", null)
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "RAMOS", "LEOCADIO", "RAMOS LEOCADIO", null)
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "SANCHEZ", "HUGO", "SANCHEZ HUGO", "36621974")
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "SOLOZA", "LUIS", "SOLOZA LUIS", null)
            empleadoDao.corregirEmpleadoPorNombre("RUTA 5", "VILLAGRAN", "PEDRO", "VILLAGRAN PEDRO", null)
            
            // ===== VIALSA =====
            Log.d("CorregirDBDirecto", "Corrigiendo VIALSA...")
            empleadoDao.corregirEmpleadoPorNombre("VIALSA", "CASASOLA", "JORGE", "CASASOLA JORGE HECTOR", "2725285")
            empleadoDao.corregirEmpleadoPorNombre("VIALSA", "CASASOLA", "WALTER", "CASASOLA WALTER MARINO", "26401152")
            empleadoDao.corregirEmpleadoPorNombre("VIALSA", "VILLA", "HECTOR", "VILLA HECTOR DANIEL", "35778402")
            empleadoDao.corregirEmpleadoPorNombre("VIALSA", "GARNICA", "EDUARDO", "GARNICA EDUARDO", "32434296")
            empleadoDao.corregirEmpleadoPorNombre("VIALSA", "PAZ", "MARTIN", "PAZ MARTIN", "21933166")
            
            // ===== CONSTRUCCION =====
            Log.d("CorregirDBDirecto", "Corrigiendo CONSTRUCCION...")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "CUELLAR", "FACUNDO", "CUELLAR FACUNDO", "38650700")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "AHUMADA", "JAVIER", "AHUMADA JAVIER", null)
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "BRIZUELA", "SANDRO", "BRIZUELA SANDRO", "22492209")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "CESPEDES", "GONZALO", "CESPEDES GONZALO", null)
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "COHAN", "ANTONIO", "COHAN ANTONIO", null)
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "CHILANGO", "ANDROSIO", "CHILANGO ANDROSIO", null)
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "CUJUJUBA", "ROQUE", "CUJUJUBA ROQUE", "16970546")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "ESPINOZA", "RODOLFO", "ESPINOZA RODOLFO", "14916117")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "GUANTAY", "ALVARO", "GUANTAY ALVARO", "38042465")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "GUANTAY", "FEDERICO", "GUANTAY FEDERICO", "36951679")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "HUERTA", "NICOLAS", "HUERTA NICOLAS", "39400694")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "IBAÑEZ", "NANCY", "IBAÑEZ NANCY", "24898307")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "MARTIN", "RUBEN", "MARTIN RUBEN", "23385514")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "MEDINA", "ADRIAN", "MEDINA ADRIAN", null)
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "MOYA", "PEDRO", "MOYA PEDRO", "16688040")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "NUÑEZ", "PABLO", "NUÑEZ PABLO", "39217740")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "PAZ", "EVELIO", "PAZ EVELIO JESUS", "23680313")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "OLIVERA", "BERTO", "OLIVERA BERTO", "31496973")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "OSTRIANO", "JOAQUIN", "OSTRIANO JOAQUIN", "30190455")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "SALDNIA", "ALEXANDER", "SALDNIA ALEXANDER", "39596641")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "TERCEROS", "JAVIER", "TERCEROS JAVIER", "26179539")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "VEGA", "JULIAN", "VEGA JULIAN", "27547361")
            empleadoDao.corregirEmpleadoPorNombre("CONSTRUCCION", "VELARDE", "RAMIRO", "VELARDE RAMIRO", "38036002")
            
            // ===== CUCHUY =====
            Log.d("CorregirDBDirecto", "Corrigiendo CUCHUY...")
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "ANGEL", "JAVIER", "ANGEL JAVIER", null)
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "ARAO", "MARCELO", "ARAO MARCELO ALEJANDRO", null)
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "AYLAN", "EZEQUIEL", "AYLAN EZEQUIEL", null)
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "BRANDAN", "PEDRO", "BRANDAN PEDRO", "39364061")
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "FERNANDEZ", "ADRIAN", "FERNANDEZ ADRIAN", null)
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "GONGORA", "LUIS", "GONGORA LUIS", "37538315")
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "LLAMANI", "MAXIMILIANO", "LLAMANI MAXIMILIANO", null)
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "MOLINA", "CLAUDIO", "MOLINA CLAUDIO", null)
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "PONCE", "JORGE", "PONCE JORGE ADRIAN", null)
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "RAMOS", "ALEJANDRO", "RAMOS ALEJANDRO", "32292665")
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "ROJAS", "MARIO", "ROJAS MARIO", "42488208")
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "SORIA", "EMANUEL", "SORIA EMANUEL", "40966366")
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "TEVES", "ESTEBAN", "TEVES ESTEBAN", "44566024")
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "TEVEZ", "FERNANDO", "TEVEZ FERNANDO", "42488275")
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "TORRES", "GERARDO", "TORRES GERARDO", "44910813")
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "TORRES", "HECTOR", "TORRES HECTOR", "40150119")
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "RAMOS", "JOSE", "RAMOS JOSE", "32292665")
            empleadoDao.corregirEmpleadoPorNombre("CUCHUY", "JUAREZ", "GUSTAVO", "JUAREZ GUSTAVO", "30675083")
            
            // ===== MOSCONI =====
            Log.d("CorregirDBDirecto", "Corrigiendo MOSCONI...")
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "ARANDA", "OLGA", "ARANDA OLGA", "20877325")
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "ACOSTA", "MATIAS", "ACOSTA MATIAS", "18892104")
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "CHIPIPI", "OCTAVIO", "CHIPIPI OCTAVIO", "45116276")
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "ROBLE", "LUIS", "ROBLE LUIS", "32893473")
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "REINALDO", "BENJAMIN", "REINALDO BENJAMIN", null)
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "SEGUNDO", "JUAN", "SEGUNDO JUAN BERN", "31346481")
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "SEGUNDO", "MARIANO", "SEGUNDO MARIANO", null)
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "SEGUNDO", "CARLOS", "SEGUNDO CARLOS", null)
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "SEGUNDO", "ELIAS", "SEGUNDO ELIAS", "40329769")
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "MARTINEZ", "WILFREDO", "MARTINEZ WILFREDO", "40326244")
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "LUCIANO", "BALDINO", "LUCIANO BALDINO", null)
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "ROJAS", "MAXIMILIANO", "ROJAS MAXIMILIANO ANT", null)
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "FLORES", "FABIO", "FLORES FABIO", "19037217")
            empleadoDao.corregirEmpleadoPorNombre("MOSCONI", "ARANDA", "GABRIEL", "ARANDA GABRIEL", "35782469")
            
            // ===== PICADO Y COSECHA =====
            Log.d("CorregirDBDirecto", "Corrigiendo PICADO Y COSECHA...")
            empleadoDao.corregirEmpleadoPorNombre("PICADO Y COSECHA", "ARIAS", "EULOGIO", "ARIAS EULOGIO ANTONIO", "41734306")
            empleadoDao.corregirEmpleadoPorNombre("PICADO Y COSECHA", "ABAN", "SERGIO", "ABAN SERGIO", "27522020")
            empleadoDao.corregirEmpleadoPorNombre("PICADO Y COSECHA", "ALDERETE", "IVAN", "ALDERETE IVAN", "42897722")
            empleadoDao.corregirEmpleadoPorNombre("PICADO Y COSECHA", "ANACHURI", "JOSE", "ANACHURI JOSE", "33056047")
            empleadoDao.corregirEmpleadoPorNombre("PICADO Y COSECHA", "CAMPOS", "ISIDRO", "CAMPOS ISIDRO", "18737938")
            empleadoDao.corregirEmpleadoPorNombre("PICADO Y COSECHA", "CRUZ", "DANILO", "CRUZ DANILO", "27169429")
            empleadoDao.corregirEmpleadoPorNombre("PICADO Y COSECHA", "MONTES", "VICTOR", "MONTES VICTOR", "24169748")
            empleadoDao.corregirEmpleadoPorNombre("PICADO Y COSECHA", "TEJERINA", "CLAUDIO", "TEJERINA CLAUDIO FERNA", "32434281")
            empleadoDao.corregirEmpleadoPorNombre("PICADO Y COSECHA", "RODRIGUEZ", "JUAN", "RODRIGUEZ JUAN CARLOS", "37420050")
            
            Log.d("CorregirDBDirecto", "✅ === CORRECCIÓN COMPLETADA ===")
            
        } catch (e: Exception) {
            Log.e("CorregirDBDirecto", "❌ Error corrigiendo empleados", e)
        }
    }
}

