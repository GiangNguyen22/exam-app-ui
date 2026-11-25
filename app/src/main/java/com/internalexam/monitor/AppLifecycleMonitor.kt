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
        ProctoringEventBuffer.start(examId)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun stop(flushBuffer: Boolean = true) {
        this.isInExam = false
        this.examId = null
        ProctoringEventBuffer.stop(flushNow = flushBuffer)
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        if (!isInExam) return
        ProctoringEventBuffer.push(SecurityEventType.APP_BACKGROUND)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (!isInExam) return
        ProctoringEventBuffer.push(SecurityEventType.APP_FOREGROUND)
    }
}
