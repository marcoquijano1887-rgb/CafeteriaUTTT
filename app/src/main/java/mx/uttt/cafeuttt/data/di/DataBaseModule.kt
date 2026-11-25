package mx.uttt.cafeuttt.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import mx.uttt.cafeuttt.data.database.AppDatabase
import mx.uttt.cafeuttt.data.database.dao.*
import mx.uttt.cafeuttt.data.security.PasswordHasher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Singleton
    @Provides
    fun getInstance(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "cafeuttt.db")
            .addCallback(object : RoomDatabase.Callback() {
                /** Datos iniciales: se ejecutan una sola vez, al crear la base. */
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL("INSERT INTO protein_options(name, extra) VALUES ('Pollo',0),('Huevo',5),('Sin proteína',-8)")
                    db.execSQL("INSERT INTO coupons(code, percent, active) VALUES ('UTTT10',10,1)")
                    db.execSQL("""INSERT INTO products(name, description, price, category, image, favorite) VALUES
                        ('Chilaquiles verdes','Totopos bañados en salsa verde, con pollo deshebrado, crema, queso y cebolla.',45,'Desayunos','🍽️',1),
                        ('Café americano','Café de grano recién molido, 8 oz.',20,'Bebidas','☕',0),
                        ('Torta de jamón','Pan crujiente con jamón, queso y aguacate.',38,'Comidas','🥪',0),
                        ('Hot cakes','Tres hot cakes esponjosos con miel y mantequilla.',35,'Desayunos','🥞',0),
                        ('Galletas de avena','Galletas caseras de avena con pasas.',15,'Snacks','🍪',0)""")
                    // Usuario de prueba. Contraseña de demostración: uttt1234
                    db.execSQL(
                        "INSERT INTO users(name, email, password_hash, balance, balance_updated) VALUES (?,?,?,?,?)",
                        arrayOf<Any?>("Andrea Martínez", "20240182@uttt.edu.mx", PasswordHasher.hash("uttt1234"), 210, "hoy, 9:40")
                    )
                }
            })
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()
    @Provides fun provideProteinOptionDao(db: AppDatabase): ProteinOptionDao = db.proteinOptionDao()
    @Provides fun provideCouponDao(db: AppDatabase): CouponDao = db.couponDao()
    @Provides fun provideCartDao(db: AppDatabase): CartDao = db.cartDao()
    @Provides fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()
    @Provides fun provideOrderItemDao(db: AppDatabase): OrderItemDao = db.orderItemDao()
}
