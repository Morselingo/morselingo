package at.aau.morselingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import at.aau.morselingo.navigation.AppNavBar
import at.aau.morselingo.navigation.AppNavHost
import at.aau.morselingo.ui.theme.MorselingoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MorselingoTheme {
               Morselingo()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Morselingo() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
        //TODO: add topbar support with changing title depending on screen
//            TopAppBar(
//               title = { Text("Test") },
//            )
        },
        bottomBar = {
            AppNavBar(navController = navController)
        },
        modifier = Modifier.fillMaxSize()
    ) {
        innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}