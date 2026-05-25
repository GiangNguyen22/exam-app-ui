package com.internalexam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.internalexam.navigation.InternalExamApp
import com.internalexam.ui.theme.InternalExamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InternalExamTheme {
                InternalExamApp()
            }
        }
    }
}
