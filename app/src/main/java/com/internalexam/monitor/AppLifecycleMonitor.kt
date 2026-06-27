package com.internalexam.monitor

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

object AppLifecycleMonitor : DefaultLifecycleObserver {

    private var isInExam = false
    private var examId: Long? = null

    fun start(examId: Long) {
        this.isInExam = true
        this.examId = examId
        ExamEventBuffer.start(examId)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun stop(flushBuffer: Boolean = true) {
        this.isInExam = false
        this.examId = null
        ExamEventBuffer.stop(flushNow = flushBuffer)
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        if (!isInExam) return
        ExamEventBuffer.push("FOCUS_LOST", "App went to background during exam")
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (!isInExam) return
        ExamEventBuffer.push("FOCUS_RESTORED", "App returned to foreground during exam")
    }
}
