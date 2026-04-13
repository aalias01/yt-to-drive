package com.yttodrive.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yttodrive.app.databinding.ActivityFolderPickerBinding

/**
 * Will list Google Drive folders under the configured Music root and let the user pick one.
 */
class FolderPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFolderPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val shared = intent.getStringExtra(EXTRA_SHARED_TEXT).orEmpty()
        binding.summary.text = shared.ifEmpty { getString(R.string.share_no_url) }
    }

    companion object {
        const val EXTRA_SHARED_TEXT = "extra_shared_text"
    }
}
