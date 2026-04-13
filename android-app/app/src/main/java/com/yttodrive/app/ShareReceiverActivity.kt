package com.yttodrive.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yttodrive.app.databinding.ActivityShareReceiverBinding

/**
 * Entry when the user shares a URL (e.g. from YouTube) to this app.
 * Next steps: Google Sign-In, folder picker, WorkManager conversion + Drive upload.
 */
class ShareReceiverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareReceiverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val shared = intent?.getStringExtra(Intent.EXTRA_TEXT)?.trim().orEmpty()
        if (shared.isEmpty()) {
            binding.sharedText.text = getString(R.string.share_no_url)
            binding.continueButton.isEnabled = false
            return
        }

        binding.sharedText.text = shared

        binding.continueButton.setOnClickListener {
            startActivity(
                Intent(this, FolderPickerActivity::class.java).apply {
                    putExtra(FolderPickerActivity.EXTRA_SHARED_TEXT, shared)
                },
            )
        }
    }
}
