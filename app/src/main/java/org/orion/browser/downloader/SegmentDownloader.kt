package org.orion.browser.downloader

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.TimeUnit

data class DownloadProgress(
    val id: String,
    val filename: String,
    val progressPercent: Int,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val isCompleted: Boolean = false,
    val error: String? = null
)

object SegmentDownloader {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val _downloadStatus = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    val downloadStatus: StateFlow<Map<String, DownloadProgress>> = _downloadStatus.asStateFlow()

    suspend fun downloadMedia(
        context: Context,
        url: String,
        customFilename: String? = null,
        chunkCount: Int = 4
    ): Result<File> = withContext(Dispatchers.IO) {
        val downloadId = "DL_${System.currentTimeMillis()}"
        val sanitizedFilename = customFilename ?: "Orion_Video_${System.currentTimeMillis()}.mp4"

        try {
            // Step 1: Probe server with HEAD request to determine file size and byte range support
            val headRequest = Request.Builder().url(url).head().build()
            val headResponse = httpClient.newCall(headRequest).execute()
            val contentLength = headResponse.header("Content-Length")?.toLongOrNull() ?: -1L
            val acceptRanges = headResponse.header("Accept-Ranges")?.equals("bytes", ignoreCase = true) ?: false
            headResponse.close()

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val outputFile = File(downloadsDir, sanitizedFilename)

            if (contentLength > 0 && acceptRanges && chunkCount > 1) {
                // Multi-threaded chunk download
                val chunkSize = contentLength / chunkCount
                val raf = RandomAccessFile(outputFile, "rw")
                raf.setLength(contentLength)
                raf.close()

                val tasks = (0 until chunkCount).map { i ->
                    val startByte = i * chunkSize
                    val endByte = if (i == chunkCount - 1) contentLength - 1 else (startByte + chunkSize - 1)

                    async {
                        downloadChunk(url, outputFile, startByte, endByte)
                    }
                }

                tasks.awaitAll()
            } else {
                // Single-stream fallback
                val getRequest = Request.Builder().url(url).build()
                val response = httpClient.newCall(getRequest).execute()
                val body = response.body ?: throw IllegalStateException("Empty response body")

                body.byteStream().use { input ->
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            _downloadStatus.value = _downloadStatus.value.toMutableMap().apply {
                put(downloadId, DownloadProgress(downloadId, sanitizedFilename, 100, contentLength, contentLength, true))
            }

            Result.success(outputFile)
        } catch (e: Exception) {
            _downloadStatus.value = _downloadStatus.value.toMutableMap().apply {
                put(downloadId, DownloadProgress(downloadId, sanitizedFilename, 0, 0, 0, false, e.localizedMessage))
            }
            Result.failure(e)
        }
    }

    private fun downloadChunk(url: String, file: File, startByte: Long, endByte: Long) {
        val request = Request.Builder()
            .url(url)
            .addHeader("Range", "bytes=$startByte-$endByte")
            .build()

        val response = httpClient.newCall(request).execute()
        val inputStream = response.body?.byteStream() ?: return

        RandomAccessFile(file, "rw").use { raf ->
            raf.seek(startByte)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                raf.write(buffer, 0, bytesRead)
            }
        }
        response.close()
    }
}
