package mx.uttt.cafeuttt.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mx.uttt.cafeuttt.data.database.AppDatabase
import mx.uttt.cafeuttt.data.database.entity.UserEntity
import mx.uttt.cafeuttt.data.security.PasswordHasher
import mx.uttt.cafeuttt.domain.model.User
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserRepositoryLoginTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: UserRepositoryImp

    @Before
    fun createDatabase() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = UserRepositoryImp(database.userDao())
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun loginSoloAceptaContrasenaCorrecta() = runBlocking {
        database.userDao().insertarUsuario(
            UserEntity(
                name = "Andrea Martínez", email = "20240182@uttt.edu.mx",
                passwordHash = PasswordHasher.hash("uttt1234"), balance = 210,
                balanceUpdated = "hoy"
            )
        )

        val accepted = repository.authenticate("20240182@uttt.edu.mx", "uttt1234").first()
        val rejected = repository.authenticate("20240182@uttt.edu.mx", "incorrecta").first()

        assertEquals("Andrea Martínez", accepted?.name)
        assertNull(rejected)
    }

    @Test
    fun registroLocalAceptaCorreoNoInstitucional() = runBlocking {
        val email = "estudiante@gmail.com"
        val id = repository.register(
            User(name = "Estudiante", email = email, balance = 0, balanceUpdated = "Sin movimientos"),
            "mi-clave"
        )

        assertEquals(id, repository.findByEmail(email).first()?.id)
        assertEquals("Estudiante", repository.authenticate(email, "mi-clave").first()?.name)
        assertNull(repository.authenticate(email, "clave-incorrecta").first())
    }
}
