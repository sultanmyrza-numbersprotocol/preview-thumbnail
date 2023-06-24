package io.numbersprotocol.capturelite.plugins.previewthumbnail

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Base64
import android.util.Log
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@CapacitorPlugin(name = "PreviewThumbnail")
class PreviewThumbnailPlugin : Plugin() {
    private val implementation = PreviewThumbnail()

    private lateinit var executor: ExecutorService

    companion object {
        const val TAG = "PreviewThumbnailPlugin"
        const val DEFAULT_QUALITY = 100
        const val DEFAULT_IMAGE_FORMAT = 0
        const val DEFAULT_MAX_HEIGHT = 0
        const val DEFAULT_MAX_WIDTH = 0
        const val DEFAULT_MAX_TIME_MS = 0L
    }

    override fun load() {
        initExecutor()
    }

    /**
     * Initializes the executor service with a cached thread pool.
     *
     * The executor service is responsible for managing the execution of tasks in a separate thread.
     * It provides a thread pool that dynamically creates and reuses threads as needed.
     *
     * This method should be called during the plugin's initialization.
     *
     * Note: In Capacitor, you don't need to explicitly clean up the executor. The JavaScript runtime and Capacitor's
     * plugin management handle the cleanup automatically when the application is closed or the plugin is unloaded.
     * Therefore, you can rely on the system to clean up the executor and its associated threads.
     */
    private fun initExecutor() {
        executor = Executors.newCachedThreadPool()
    }

    @PluginMethod
    fun echo(call: PluginCall) {
        val value = call.getString("value")
        val ret = JSObject()
        ret.put("value", implementation.echo(value))
        call.resolve(ret)
    }


    @PluginMethod
    fun thumbnailFile(call: PluginCall) {
        val video = call.getString("video") ?: run {
            call.reject("Missing 'video' parameter")
            return
        }
        val thumbnailPath = call.getString("thumbnailPath") ?: run {
            call.reject("Missing 'path' parameter")
            return
        }
        val headersObj = call.getObject("headers")
        val headers = convertHeadersToHashMap(headersObj)
        val imageFormat = call.getInt("imageFormat") ?: DEFAULT_IMAGE_FORMAT
        val maxHeight = call.getInt("maxHeight") ?: DEFAULT_MAX_HEIGHT
        val maxWidth = call.getInt("maxWidth") ?: DEFAULT_MAX_WIDTH
        val timeMs = call.getLong("timeMs") ?: DEFAULT_MAX_TIME_MS
        val quality = call.getInt("quality") ?: DEFAULT_QUALITY

        executor.execute {
            try {
                val thumbnail =
                    buildThumbnailFile(
                        video,
                        headers,
                        thumbnailPath,
                        imageFormat,
                        maxHeight,
                        maxWidth,
                        timeMs,
                        quality
                    )
                call.resolve(JSObject().apply {
                    put("value", thumbnail)
                })
            } catch (e: Exception) {
                call.reject(e.localizedMessage, e)
            }
        }
    }

    @PluginMethod
    fun thumbnailData(call: PluginCall) {
        val video = call.getString("video") ?: run {
            call.reject("Missing 'video' parameter")
            return
        }
        val headersObj = call.getObject("headers")
        val headers = convertHeadersToHashMap(headersObj)
        val imageFormat = call.getInt("imageFormat") ?: DEFAULT_IMAGE_FORMAT
        val maxHeight = call.getInt("maxHeight") ?: DEFAULT_MAX_HEIGHT
        val maxWidth = call.getInt("maxWidth") ?: DEFAULT_MAX_WIDTH
        val timeMs = call.getLong("timeMs") ?: DEFAULT_MAX_TIME_MS
        val quality = call.getInt("quality") ?: DEFAULT_QUALITY

        executor.execute {
            try {
                val thumbnailData =
                    buildThumbnailData(
                        video,
                        headers,
                        imageFormat,
                        maxHeight,
                        maxWidth,
                        timeMs,
                        quality
                    )
                val thumbnailBase64 = Base64.encodeToString(thumbnailData, Base64.DEFAULT)

                call.resolve(JSObject().apply {
                    put("value", thumbnailBase64)
                })
            } catch (e: Exception) {
                call.reject(e.localizedMessage, e)
            }
        }
    }

    private fun convertHeadersToHashMap(headersObj: JSObject?): HashMap<String, String>? {
        if (headersObj == null) {
            return null
        }
        val headers = HashMap<String, String>()
        val keys = headersObj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = headersObj.getString(key)
            value?.let {
                headers[key] = it
            }
        }
        return headers
    }


    private fun intToFormat(format: Int): Bitmap.CompressFormat {
        return when (format) {
            0 -> Bitmap.CompressFormat.JPEG
            1 -> Bitmap.CompressFormat.PNG
            2 -> Bitmap.CompressFormat.WEBP
            else -> Bitmap.CompressFormat.JPEG
        }
    }

    private fun formatExt(format: Int): String {
        return when (format) {
            0 -> "jpg"
            1 -> "png"
            2 -> "webp"
            else -> "jpg"
        }
    }

    private fun buildThumbnailData(
        vidPath: String,
        headers: HashMap<String, String>?,
        format: Int,
        maxh: Int,
        maxw: Int,
        timeMs: Long,
        quality: Int
    ): ByteArray {
        val bitmap = createVideoThumbnail(vidPath, headers, maxh, maxw, timeMs)
            ?: throw NullPointerException()

        val stream = ByteArrayOutputStream()
        bitmap.compress(intToFormat(format), quality, stream)
        bitmap.recycle()
        if (bitmap == null)
            throw NullPointerException()
        return stream.toByteArray()
    }

    private fun buildThumbnailFile(
        vidPath: String,
        headers: HashMap<String, String>?,
        path: String?,
        format: Int,
        maxh: Int,
        maxw: Int,
        timeMs: Long,
        quality: Int
    ): String {
        val bytes = buildThumbnailData(vidPath, headers, format, maxh, maxw, timeMs, quality)
        val ext = formatExt(format)
        val i = vidPath.lastIndexOf(".")
        var fullpath = vidPath.substring(0, i + 1) + ext
        val isLocalFile = vidPath.startsWith("/") || vidPath.startsWith("file://")

        val context: Context = bridge.activity.applicationContext
        if (path == null && !isLocalFile) {
            fullpath = context.cacheDir.absolutePath
        }

        if (path != null) {
            fullpath = if (path.endsWith(ext)) {
                path
            } else {
                val j = fullpath.lastIndexOf("/")
                if (path.endsWith("/")) {
                    path + fullpath.substring(j + 1)
                } else {
                    path + fullpath.substring(j)
                }
            }
        }

        try {
            val f = FileOutputStream(fullpath)
            f.write(bytes)
            f.close()
            Log.d(TAG, String.format("buildThumbnailFile( written:%d )", bytes.size))
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
        return fullpath
    }


    private fun createVideoThumbnail(
        video: String?,
        headers: HashMap<String, String>?,
        targetH: Int,
        targetW: Int,
        timeMs: Long
    ): Bitmap? {
        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            if (video!!.startsWith("/")) {
                setDataSource(video, retriever)
            } else if (video.startsWith("file://")) {
                setDataSource(video.substring(7), retriever)
            } else {
                retriever.setDataSource(
                    video,
                    headers ?: HashMap<String, String>()
                )
            }

            if (targetH != 0 || targetW != 0) {
                if (android.os.Build.VERSION.SDK_INT >= 27 && targetH != 0 && targetW != 0) {
                    // API Level 27
                    bitmap = retriever.getScaledFrameAtTime(
                        timeMs * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST,
                        targetW,
                        targetH
                    )
                } else {
                    bitmap = retriever.getFrameAtTime(
                        timeMs * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST
                    )
                    if (bitmap != null) {
                        val width = bitmap.width
                        val height = bitmap.height
                        var calculatedTargetW = targetW
                        var calculatedTargetH = targetH

                        if (calculatedTargetW == 0) {
                            calculatedTargetW =
                                Math.round(((calculatedTargetH.toFloat() / height) * width))
                        }
                        if (calculatedTargetH == 0) {
                            calculatedTargetH =
                                Math.round(((calculatedTargetW.toFloat() / width) * height))
                        }
                        Log.d(
                            TAG,
                            String.format(
                                "original w:%d, h:%d => %d, %d",
                                width,
                                height,
                                calculatedTargetW,
                                calculatedTargetH
                            )
                        )
                        bitmap = Bitmap.createScaledBitmap(
                            bitmap,
                            calculatedTargetW,
                            calculatedTargetH,
                            true
                        )
                    }
                }
            } else {
                bitmap =
                    retriever.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST)
            }
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        } catch (ex: RuntimeException) {
            ex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (ex: RuntimeException) {
                ex.printStackTrace()
            }
        }
        return bitmap
    }

    private fun setDataSource(video: String, retriever: MediaMetadataRetriever) {
        val videoFile = java.io.File(video)
        val inputStream = java.io.FileInputStream(videoFile.absolutePath)
        retriever.setDataSource(inputStream.fd)
    }


}