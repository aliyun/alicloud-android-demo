package com.alibaba.httpdns.android.demo.practice.exoplayer

import com.alibaba.httpdns.android.demo.BaseFragment
import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.ExoPlayerCaseBinding
import android.os.Bundle
import android.view.View
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.ClippingConfiguration
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import java.net.URL

/**
 * ExoPlayer最佳实践
 * @author 任伟
 * @date 2024/07/22
 */
class ExoPlayerCaseFragment : BaseFragment<ExoPlayerCaseBinding>() {

    private lateinit var viewModel: ExoPlayerCaseViewModel
    private lateinit var player: Player

    override fun getLayoutId(): Int {
        return R.layout.httpdns_fragment_exo_player_case
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ExoPlayerCaseViewModel::class.java]
        viewModel.initData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        player = ExoPlayer.Builder(requireContext())
            .setMediaSourceFactory(createMediaDataSourceFactory())
            .build()
        binding.playerView.player = player

        binding.btnStartPlay.setOnClickListener {
            startPlay()
        }
    }

    /**
     * 开始播放
     */
    private fun startPlay() {
        viewModel.showRequestAndResolveResult.value = true
        viewModel.host.value = URL(viewModel.playerUrl.value).host
        val mediaItem = MediaItem.Builder()
            .setUri(viewModel.playerUrl.value)
            .setMediaMetadata(MediaMetadata.Builder().setTitle("HD (MP4, H264)").build())
            .setClippingConfiguration(
                ClippingConfiguration.Builder()
                    .setStartPositionMs(0)
                    .setEndPositionMs(Long.MIN_VALUE)
                    .build()
            ).build()
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    /**
     * 创建mediaPlayer网络请求的DataSource
     */
    @OptIn(markerClass = [UnstableApi::class])
    fun createMediaDataSourceFactory(): MediaSource.Factory {
        return DefaultMediaSourceFactory(
            OkHttpDataSource.Factory {
                viewModel.okHttpClient.newCall(it)
            }
        )
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            player.pause()
        } else {
            player.play()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
    }

}