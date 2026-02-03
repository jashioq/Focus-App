import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import data.dataSource.dataStore.DATA_STORE_FILE_NAME
import data.dataSource.dataStore.createDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun createIosDataStore(): DataStore<Preferences> {
    return createDataStore {
        val directory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(directory).path + "/$DATA_STORE_FILE_NAME"
    }
}