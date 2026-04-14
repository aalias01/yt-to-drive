package com.yttodrive.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.snackbar.Snackbar
import com.yttodrive.app.databinding.ActivityFilenameBinding
import com.yttodrive.app.util.FilenameSuggest
import com.yttodrive.app.work.ConvertUploadWorker

/**
 * Lets the user edit the output MP3 base name, then enqueues [ConvertUploadWorker].
 * Download / encode / upload will be implemented in the worker next.
 */
class FilenameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilenameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilenameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sourceUrl = intent.getStringExtra(EXTRA_SOURCE_URL).orEmpty()
        val folderId = intent.getStringExtra(EXTRA_FOLDER_ID).orEmpty()
        val folderName = intent.getStringExtra(EXTRA_FOLDER_NAME).orEmpty()

        if (sourceUrl.isEmpty() || folderId.isEmpty()) {
            Snackbar.make(binding.root, R.string.filename_missing_data, Snackbar.LENGTH_LONG).show()
            finish()
            return
        }

        binding.contextSummary.text = getString(
            R.string.filename_context_fmt,
            folderName.ifEmpty { folderId.take(12) + "…" },
            sourceUrl.take(120) + if (sourceUrl.length > 120) "…" else "",
        )

        val suggested = FilenameSuggest.suggestBasename(sourceUrl)
        binding.fileBaseName.setText(suggested)
        binding.fileBaseName.setSelection(binding.fileBaseName.text?.length ?: 0)

        binding.fileBaseName.doAfterTextChanged {
            binding.confirmButton.isEnabled = !binding.fileBaseName.text.isNullOrBlank()
        }
        binding.confirmButton.isEnabled = suggested.isNotBlank()

        binding.confirmButton.setOnClickListener {
            val raw = binding.fileBaseName.text?.toString().orEmpty()
            val base = FilenameSuggest.sanitize(raw.removeSuffix(".mp3").trim())
            if (base.isEmpty()) {
                Snackbar.make(binding.root, R.string.filename_empty, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = workDataOf(
                ConvertUploadWorker.KEY_SOURCE_URL to sourceUrl,
                ConvertUploadWorker.KEY_FOLDER_ID to folderId,
                ConvertUploadWorker.KEY_OUTPUT_BASENAME to base,
            )
            val request = OneTimeWorkRequestBuilder<ConvertUploadWorker>()
                .setInputData(data)
                .build()
            WorkManager.getInstance(applicationContext).enqueue(request)

            Toast.makeText(this, R.string.filename_queued, Toast.LENGTH_SHORT).show()
            finishAffinity()
        }
    }

    companion object {
        const val EXTRA_SOURCE_URL = "extra_source_url"
        const val EXTRA_FOLDER_ID = "extra_folder_id"
        const val EXTRA_FOLDER_NAME = "extra_folder_name"
    }
}
