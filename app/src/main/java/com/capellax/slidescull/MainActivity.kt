package com.capellax.slidescull

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.capellax.slidescull.ui.navigation.AppNavGraph
import com.capellax.slidescull.ui.theme.SlideScullTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            SlideScullTheme {
                AppNavGraph(navController = navController)
            }
        }
    }
}
