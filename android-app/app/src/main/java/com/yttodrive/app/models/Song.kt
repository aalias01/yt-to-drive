package com.yttodrive.app.models

/**
 * Metadata used when naming the output MP3 (ID3 or fallback title).
 */
data class Song(
    val title: String,
    val artist: String?,
    val suggestedFileBaseName: String,
)
