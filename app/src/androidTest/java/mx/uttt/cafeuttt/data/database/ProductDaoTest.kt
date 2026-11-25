package mx.uttt.cafeuttt.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mx.uttt.cafeuttt.data.database.entity.ProductEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {
    private lateinit var database: AppDatabase

    @Before
    fun createDatabase() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun insertarYConsultarProductoPorCategoriaYNombre() = runBlocking {
        database.productDao().insert(
            ProductEntity(name = "Café americano", description = "Café", price = 20, category = "Bebidas", image = "☕")
        )

        val result = database.productDao().observeByCategoryAndName("Bebidas", "americano").first()

        assertEquals(1, result.size)
        assertEquals("Café americano", result.single().name)
    }
}
