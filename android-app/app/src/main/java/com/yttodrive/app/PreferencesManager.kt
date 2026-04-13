package com.yttodrive.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "yt_to_drive_prefs")

object PreferencesManager {

    private val KEY_LAST_FOLDER_ID = stringPreferencesKey("last_folder_id")
    private val KEY_LAST_FOLDER_NAME = stringPreferencesKey("last_folder_name")

    fun lastFolderId(context: Context): Flow<String?> =
        context.dataStore.data.map { it[KEY_LAST_FOLDER_ID] }

    fun lastFolderName(context: Context): Flow<String?> =
        context.dataStore.data.map { it[KEY_LAST_FOLDER_NAME] }

    suspend fun setLastFolder(context: Context, id: String, name: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_FOLDER_ID] = id
            prefs[KEY_LAST_FOLDER_NAME] = name
        }
    }
}
