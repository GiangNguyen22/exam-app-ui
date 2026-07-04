package com.internalexam

import android.view.WindowManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.internalexam.data.ExamAttemptStore
import com.internalexam.data.SessionManager
import com.internalexam.monitor.ProctoringEventBuffer
import com.internalexam.monitor.NetworkMonitor
import com.internalexam.navigation.InternalExamApp
import com.internalexam.ui.theme.InternalExamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        SessionManager.initialize(applicationContext)
        ExamAttemptStore.initialize(applicationContext)
        NetworkMonitor.initialize(applicationContext)
        ProctoringEventBuffer.initialize(applicationContext)
        ExamAttemptStore.restoreState()
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                ExamAttemptStore.saveState()
            }
        })

        setContent {
            InternalExamTheme {
                InternalExamApp()
            }
        }
    }
}
