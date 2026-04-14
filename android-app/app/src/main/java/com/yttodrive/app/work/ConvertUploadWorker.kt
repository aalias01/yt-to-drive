package com.yttodrive.app.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Background job: extract audio, encode MP3, upload to Drive.
 * Input is provided via [inputData]; pipeline body is added in a later change.
 */
class ConvertUploadWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val url = inputData.getString(KEY_SOURCE_URL)
        val folderId = inputData.getString(KEY_FOLDER_ID)
        val basename = inputData.getString(KEY_OUTPUT_BASENAME)
        if (url.isNullOrBlank() || folderId.isNullOrBlank() || basename.isNullOrBlank()) {
            Log.w(
                TAG,
                "Missing input url=${url != null} folder=${folderId != null} basename=${basename != null}",
            )
            return Result.failure()
        }
        Log.i(
            TAG,
            "Pipeline stub: folderId=${folderId.take(12)}… basename=$basename url=${url.take(96)}",
        )
        // TODO: NewPipeExtractor / yt-dlp → FFmpeg → Drive multipart upload
        return Result.success()
    }

    companion object {
        private const val TAG = "ConvertUploadWorker"

        const val KEY_SOURCE_URL = "source_url"
        const val KEY_FOLDER_ID = "folder_id"
        const val KEY_OUTPUT_BASENAME = "output_basename"
    }
}
