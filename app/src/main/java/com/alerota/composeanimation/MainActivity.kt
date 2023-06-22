package com.alerota.composeanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.alerota.composeanimation.header.SheetPw
import com.alerota.composeanimation.ui.theme.ComposeAnimationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable full screen mode to draw under the status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ComposeAnimationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SheetPw()
                }
            }
        }
    }
}

