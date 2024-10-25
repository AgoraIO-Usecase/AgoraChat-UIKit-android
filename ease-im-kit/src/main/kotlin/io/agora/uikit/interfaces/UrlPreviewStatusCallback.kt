package io.agora.uikit.interfaces

import io.agora.uikit.model.EasePreview

interface UrlPreviewStatusCallback {
    fun onParsing(){}
    fun onParseFile(){}
    fun onDownloadStart(){}
    fun onDownloadFinish(preview: EasePreview?){}
}