package com.adoptme.app.data.local

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class PetPhotoStore(context: Context) {
    private val appContext = context.applicationContext

    suspend fun persist(uri: Uri): String = withContext(Dispatchers.IO) {
        val dir = File(appContext.filesDir, "pet_photos").apply { mkdirs() }
        val destination = File(dir, "${UUID.randomUUID()}.jpg")
        val input = appContext.contentResolver.openInputStream(uri)
            ?: error("Не удалось прочитать выбранное фото.")
        input.use { stream ->
            destination.outputStream().use { output -> stream.copyTo(output) }
        }
        Uri.fromFile(destination).toString()
    }
}
