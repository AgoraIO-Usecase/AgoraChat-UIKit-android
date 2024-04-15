package com.hyphenate.easeui.common.player

interface EasyVideoCallback {
    fun onStarted(player: EasyVideoPlayer?)
    fun onPaused(player: EasyVideoPlayer?)
    fun onPreparing(player: EasyVideoPlayer?)
    fun onPrepared(player: EasyVideoPlayer?)
    fun onBuffering(percent: Int)
    fun onError(player: EasyVideoPlayer?, e: Exception?)
    fun onCompletion(player: EasyVideoPlayer?)
    fun onClickVideoFrame(player: EasyVideoPlayer?)
}