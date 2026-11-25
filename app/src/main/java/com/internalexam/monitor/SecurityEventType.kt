package com.internalexam.monitor

enum class SecurityEventType(val rawValue: String, val description: String) {
    APP_BACKGROUND("APP_BACKGROUND", "Thí sinh rời khỏi ứng dụng"),
    APP_FOREGROUND("APP_FOREGROUND", "Thí sinh quay lại ứng dụng"),
    SCREENSHOT_ATTEMPT("SCREENSHOT_ATTEMPT", "Phát hiện thao tác chụp màn hình"),
    CONNECTION_LOST("CONNECTION_LOST", "Mất kết nối mạng"),
    CONNECTION_RESTORED("CONNECTION_RESTORED", "Khôi phục kết nối mạng"),
    SPLIT_SCREEN_DETECTED("SPLIT_SCREEN_DETECTED", "Phát hiện chế độ chia đôi màn hình");

    companion object {
        fun fromString(value: String?): SecurityEventType? {
            if (value.isNullOrBlank()) return null
            return when (value.trim().uppercase()) {
                "FOCUS_LOST", "APP_BACKGROUND" -> APP_BACKGROUND
                "FOCUS_RESTORED", "APP_FOREGROUND" -> APP_FOREGROUND
                "SCREENSHOT", "SCREENSHOT_ATTEMPT" -> SCREENSHOT_ATTEMPT
                "CONNECTION_LOST" -> CONNECTION_LOST
                "CONNECTION_RESTORED" -> CONNECTION_RESTORED
                "SPLIT_SCREEN", "SPLIT_SCREEN_DETECTED" -> SPLIT_SCREEN_DETECTED
                else -> values().firstOrNull { it.name.equals(value, ignoreCase = true) }
            }
        }
    }
}
