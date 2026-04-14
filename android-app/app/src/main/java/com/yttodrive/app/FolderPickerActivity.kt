package com.yttodrive.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.yttodrive.app.auth.GoogleSignInConfig
import com.yttodrive.app.databinding.ActivityFolderPickerBinding
import com.yttodrive.app.drive.DriveRepository
import com.yttodrive.app.drive.DriveServiceFactory
import com.yttodrive.app.models.DriveFolder
import kotlinx.coroutines.launch

/**
 * Lists folders inside the user's Google Drive **Music** folder (see project plan).
 * Requires Google Sign-In and Drive API access configured in Google Cloud Console.
 */
class FolderPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFolderPickerBinding
    private lateinit var listAdapter: FolderListAdapter

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java) ?: return@registerForActivityResult
                onSignedIn(account)
            } catch (e: ApiException) {
                showStatus(getString(R.string.sign_in_failed, e.statusCode))
            }
        }

    private val recoveryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            loadFolders()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val shared = intent.getStringExtra(EXTRA_SHARED_TEXT).orEmpty()
        binding.summary.text =
            shared.ifEmpty { getString(R.string.folder_picker_no_shared_link) }

        listAdapter = FolderListAdapter { folder -> onFolderChosen(folder) }
        binding.folderList.layoutManager = LinearLayoutManager(this)
        binding.folderList.adapter = listAdapter

        binding.signInButton.setOnClickListener {
            val client = GoogleSignIn.getClient(this, GoogleSignInConfig.googleSignInOptions())
            signInLauncher.launch(client.signInIntent)
        }

        binding.refresh.setOnRefreshListener { loadFolders() }

        val existing = GoogleSignIn.getLastSignedInAccount(this)
        if (existing != null) {
            binding.signInButton.visibility = View.GONE
            onSignedIn(existing)
        }
    }

    private fun onSignedIn(account: GoogleSignInAccount) {
        binding.signInButton.visibility = View.GONE
        showStatus(getString(R.string.signed_in_as, account.email ?: account.displayName.orEmpty()))
        loadFolders()
    }

    private fun loadFolders() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            binding.refresh.isRefreshing = false
            binding.signInButton.visibility = View.VISIBLE
            showStatus(getString(R.string.sign_in_required))
            return
        }

        binding.progress.visibility = View.VISIBLE
        binding.refresh.isRefreshing = true
        showStatus(getString(R.string.loading_folders))

        val drive = DriveServiceFactory.create(this, account)
        lifecycleScope.launch {
            try {
                val musicId = DriveRepository.findMusicFolderId(drive)
                if (musicId == null) {
                    listAdapter.submitList(emptyList())
                    showStatus(getString(R.string.music_folder_missing))
                    return@launch
                }
                val children = DriveRepository.listChildFolders(drive, musicId)
                listAdapter.submitList(children)
                showStatus(
                    getString(R.string.folders_under_music, children.size),
                )
            } catch (e: UserRecoverableAuthIOException) {
                recoveryLauncher.launch(e.intent)
            } catch (e: Exception) {
                listAdapter.submitList(emptyList())
                showStatus(e.message ?: e.javaClass.simpleName)
            } finally {
                binding.progress.visibility = View.GONE
                binding.refresh.isRefreshing = false
            }
        }
    }

    private fun onFolderChosen(folder: DriveFolder) {
        lifecycleScope.launch {
            PreferencesManager.setLastFolder(this@FolderPickerActivity, folder.id, folder.name)
            val shared = intent.getStringExtra(EXTRA_SHARED_TEXT).orEmpty()
            if (shared.isNotEmpty()) {
                startActivity(
                    Intent(this@FolderPickerActivity, FilenameActivity::class.java).apply {
                        putExtra(FilenameActivity.EXTRA_SOURCE_URL, shared)
                        putExtra(FilenameActivity.EXTRA_FOLDER_ID, folder.id)
                        putExtra(FilenameActivity.EXTRA_FOLDER_NAME, folder.name)
                    },
                )
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.folder_saved, folder.name),
                    Snackbar.LENGTH_LONG,
                ).show()
            }
        }
    }

    private fun showStatus(message: String) {
        binding.statusText.text = message
    }

    companion object {
        const val EXTRA_SHARED_TEXT = "extra_shared_text"
    }
}
