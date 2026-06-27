package com.internalexam.monitor

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.AuditLogCreateRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object ExamEventBuffer {

    private var prefs: SharedPreferences? = null
    private var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var flushJob: Job? = null
    private var examId: Long? = null
    private val gson = Gson()

    private const val PREFS_NAME = "exam_event_buffer"
    private const val KEY_EVENTS = "pending_events"
    private const val FLUSH_INTERVAL_MS = 30_000L
    private const val MAX_BATCH_SIZE = 10

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun start(eid: Long) {
        examId = eid
        flushJob?.cancel()
        flushJob = scope.launch {
            while (isActive) {
                delay(FLUSH_INTERVAL_MS)
                flush()
            }
        }
    }

    fun stop(flushNow: Boolean = false) {
        flushJob?.cancel()
        flushJob = null
        if (flushNow) flush()
        examId = null
    }

    fun push(action: String, reason: String) {
        val eid = examId ?: return
        val event = AuditLogCreateRequest(
            action = action,
            resourceType = "EXAM",
            resourceId = eid,
            reason = reason
        )
        persistEvent(event)
        if (pendingEventCount() >= MAX_BATCH_SIZE) {
            flush()
        }
    }

    fun flush() {
        val auth = SessionManager.authorizationHeader() ?: return
        val events = takePendingEvents(MAX_BATCH_SIZE)
        if (events.isEmpty()) return
        runBlocking(Dispatchers.IO) {
            val remaining = mutableListOf<AuditLogCreateRequest>()
            events.forEach { event ->
                val result = runCatching { ApiClient.createAuditLog(auth, event) }
                if (result.isFailure) {
                    remaining.add(event)
                }
            }
            if (remaining.isNotEmpty()) {
                remaining.forEach { persistEvent(it) }
            }
        }
    }

    private fun persistEvent(event: AuditLogCreateRequest) {
        val p = prefs ?: return
        val events = loadEvents()
        events.add(event)
        p.edit().putString(KEY_EVENTS, gson.toJson(events)).apply()
    }

    private fun pendingEventCount(): Int {
        return loadEvents().size
    }

    private fun takePendingEvents(max: Int): List<AuditLogCreateRequest> {
        val p = prefs ?: return emptyList()
        val events = loadEvents()
        if (events.isEmpty()) return emptyList()
        val batch = events.take(max)
        val remaining = events.drop(max)
        p.edit().putString(KEY_EVENTS, if (remaining.isEmpty()) null else gson.toJson(remaining)).apply()
        return batch
    }

    private fun loadEvents(): MutableList<AuditLogCreateRequest> {
        val p = prefs ?: return mutableListOf()
        val json = p.getString(KEY_EVENTS, null) ?: return mutableListOf()
        return try {
            val type = object : TypeToken<MutableList<AuditLogCreateRequest>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            p.edit().remove(KEY_EVENTS).apply()
            mutableListOf()
        }
    }
}
