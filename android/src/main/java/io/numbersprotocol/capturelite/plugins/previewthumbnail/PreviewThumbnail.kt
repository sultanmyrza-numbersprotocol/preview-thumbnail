package io.numbersprotocol.capturelite.plugins.previewthumbnail

import android.util.Log

class PreviewThumbnail {
    fun echo(value: String?): String? {
        Log.i("Echo", value!!)
        return value
    }
}