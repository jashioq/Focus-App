package com.jan.focus

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import data.dataSource.dataStore.DATA_STORE_FILE_NAME
import data.dataSource.dataStore.createDataStore

fun createAndroidDataStore(context: Context): DataStore<Preferences> {
    return createDataStore {
        context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
    }
}
