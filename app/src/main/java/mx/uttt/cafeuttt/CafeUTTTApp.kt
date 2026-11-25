package mx.uttt.cafeuttt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import mx.uttt.cafeuttt.data.database.AppDatabase

@HiltAndroidApp
class CafeUTTTApp : Application() {
    @Inject lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        // Abre Room fuera del hilo principal para que el callback de seed se ejecute al instalar.
        Thread({ database.openHelper.writableDatabase }, "CafeUTTT-db-init").apply {
            isDaemon = true
            start()
        }
    }
}
