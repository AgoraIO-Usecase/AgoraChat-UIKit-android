package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.model.EasePreview

interface UrlPreviewStatusCallback {
    fun onParsing(){}
    fun onParseFile(){}
    fun onDownloadStart(){}
    fun onDownloadFinish(preview: EasePreview?){}
}