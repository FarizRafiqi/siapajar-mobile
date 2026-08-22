package id.siapajar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import id.siapajar.app.theme.SiapAjarTheme
import id.siapajar.app.ui.navigation.SiapAjarNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SiapAjarTheme {
                SiapAjarNavGraph()
            }
        }
    }
}
