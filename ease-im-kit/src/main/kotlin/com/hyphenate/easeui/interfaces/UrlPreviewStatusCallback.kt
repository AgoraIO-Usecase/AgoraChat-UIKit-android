package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.model.ChatUIKitPreview

interface UrlPreviewStatusCallback {
    fun onParsing(){}
    fun onParseFile(){}
    fun onDownloadStart(){}
    fun onDownloadFinish(preview: ChatUIKitPreview?){}
}