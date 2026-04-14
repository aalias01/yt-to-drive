package com.yttodrive.app.drive

import com.google.api.services.drive.Drive
import com.yttodrive.app.models.DriveFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DriveRepository {

    /**
     * Finds the first folder named "Music" directly under "My Drive" (root).
     * Matches the project plan: user creates `Music` once in Drive.
     */
    suspend fun findMusicFolderId(drive: Drive): String? = withContext(Dispatchers.IO) {
        val response = drive.files().list()
            .setQ(
                "mimeType = 'application/vnd.google-apps.folder' " +
                    "and name = 'Music' " +
                    "and 'root' in parents " +
                    "and trashed = false",
            )
            .setSpaces("drive")
            .setFields("files(id, name)")
            .setPageSize(5)
            .execute()
        response.files?.firstOrNull()?.id
    }

    suspend fun listChildFolders(drive: Drive, parentFolderId: String): List<DriveFolder> =
        withContext(Dispatchers.IO) {
            val escaped = parentFolderId.replace("'", "\\'")
            val q =
                "'$escaped' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false"
            val out = mutableListOf<DriveFolder>()
            var pageToken: String? = null
            while (true) {
                val request = drive.files().list()
                    .setQ(q)
                    .setFields("nextPageToken, files(id, name)")
                    .setPageSize(100)
                pageToken?.let { request.setPageToken(it) }
                val response = request.execute()
                response.files?.forEach { f ->
                    val id = f.id ?: return@forEach
                    val name = f.name.orEmpty()
                    out += DriveFolder(id = id, name = name)
                }
                pageToken = response.nextPageToken
                if (pageToken == null) break
            }
            out.sortBy { it.name.lowercase() }
            out
        }
}
