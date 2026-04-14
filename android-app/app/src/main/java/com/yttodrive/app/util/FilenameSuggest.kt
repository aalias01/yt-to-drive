package com.yttodrive.app.util

import android.net.Uri

object FilenameSuggest {

    /**
     * Produces a safe default base name (no extension) from a shared URL.
     * YouTube: uses the `v=` id or youtu.be short id; otherwise a time-based fallback.
     */
    fun suggestBasename(url: String): String {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return "track_${System.currentTimeMillis()}"

        val uri = runCatching { Uri.parse(trimmed) }.getOrNull()
        if (uri != null) {
            val v = uri.getQueryParameter("v")
            if (!v.isNullOrBlank()) return sanitize("YouTube_$v")

            val host = uri.host?.lowercase().orEmpty()
            if (host.contains("youtu.be")) {
                val seg = uri.lastPathSegment
                if (!seg.isNullOrBlank()) return sanitize("YouTube_$seg")
            }
        }

        Regex("(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{6,})")
            .find(trimmed)
            ?.groupValues
            ?.getOrNull(1)
            ?.let { return sanitize("YouTube_$it") }

        return sanitize("Audio_${System.currentTimeMillis()}")
    }

    /** Removes characters Google Drive disallows in file names. */
    fun sanitize(base: String): String {
        return base
            .replace(Regex("""[\\/:*?"<>|]"""), "_")
            .trim()
            .ifEmpty { "track_${System.currentTimeMillis()}" }
    }
}
