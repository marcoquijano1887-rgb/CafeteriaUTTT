package mx.uttt.cafeuttt.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import mx.uttt.cafeuttt.data.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertarUsuario(user: UserEntity): Long
    @Update
    suspend fun actualizarUsuario(user: UserEntity)
    @Delete
    suspend fun eliminarUsuario(user: UserEntity)
    /** Login: se busca por correo y luego se compara el hash en el repositorio. */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun buscarPorCorreo(email: String): Flow<UserEntity?>
    @Query("SELECT * FROM users WHERE id = :id")
    fun observarUsuario(id: Int): Flow<UserEntity?>
}
