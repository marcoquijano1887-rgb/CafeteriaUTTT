package mx.uttt.cafeuttt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import mx.uttt.cafeuttt.navigation.CafeApp
import mx.uttt.cafeuttt.ui.theme.CafeUTTTTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CafeUTTTTheme {
                CafeApp()
            }
        }
    }
}
