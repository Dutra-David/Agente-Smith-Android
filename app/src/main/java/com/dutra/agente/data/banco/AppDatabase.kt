package com.dutra.agente.data.banco
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dutra.agente.dados.banco.daos.HistoricoChatDao
import com.dutra.agente.dados.banco.daos.HistoricoInteracaoDao
import com.dutra.agente.dados.banco.daos.UsuarioPerfilDao
import com.dutra.agente.dados.banco.entidades.HistoricoChat
import com.dutra.agente.dados.banco.entidades.HistoricoInteracao
import com.dutra.agente.dados.banco.entidades.UsuarioPerfil
import com.dutra.agente.data.banco.Converters

/**
 * AppDatabase - Room Database Configuration
 * Central database management for all local data persistence
 */
@Database(
    entities = [
        UsuarioPerfil::class,
        HistoricoChat::class,
        HistoricoInteracao::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun usuarioPerfilDao(): UsuarioPerfilDao
    abstract fun historicoChatDao(): HistoricoChatDao
    abstract fun historicoInteracaoDao(): HistoricoInteracaoDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agente_smith_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
