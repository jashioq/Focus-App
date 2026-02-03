package com.jan.focus

import App
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkTheme = isSystemInDarkTheme()

            enableEdgeToEdge(
                statusBarStyle =
                if (isDarkTheme) {
                    SystemBarStyle.dark(Color.Transparent.hashCode())
                } else {
                    SystemBarStyle.light(
                        Color.Transparent.hashCode(),
                        Color.Transparent.hashCode(),
                    )
                },
                navigationBarStyle =
                if (isDarkTheme) {
                    SystemBarStyle.dark(Color.Transparent.hashCode())
                } else {
                    SystemBarStyle.light(
                        Color.Transparent.hashCode(),
                        Color.Transparent.hashCode(),
                    )
                },
            )

            @SuppressLint("SourceLockedOrientationActivity")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
