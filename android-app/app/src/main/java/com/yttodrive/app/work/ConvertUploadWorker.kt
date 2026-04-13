package com.yttodrive.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Background job: extract audio, encode MP3, upload to Drive.
 * Wired up after FFmpeg + NewPipeExtractor + Drive API integration.
 */
class ConvertUploadWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = Result.success()
}
