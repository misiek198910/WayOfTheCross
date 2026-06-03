package droga_krzyzowa.droga_krzyzowa.util

import android.content.Context
import androidx.core.content.edit

class NotificationPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("dk_notif_prefs", Context.MODE_PRIVATE)

    // Flaga: Czy użytkownik kliknął już "Później" lub "Zgadzam się"? [cite: 2026-02-17]
    var hasHandledPermissionRequest: Boolean
        get() = prefs.getBoolean("handled_permission", false)
        set(value) = prefs.edit { putBoolean("handled_permission", value) }
}