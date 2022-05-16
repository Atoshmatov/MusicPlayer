package uz.gita.musicplayer.mobdev20.presentation.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.musicplayer.mobdev20.R
import uz.gita.musicplayer.mobdev20.data.ActionEnum
import uz.gita.musicplayer.mobdev20.data.MusicData
import uz.gita.musicplayer.mobdev20.databinding.ScreenMusicplayerBinding
import uz.gita.musicplayer.mobdev20.presentation.service.MusicService
import uz.gita.musicplayer.mobdev20.utils.MyAppManager
import uz.gita.musicplayer.mobdev20.utils.setChangeProgress

@AndroidEntryPoint
class MusicPlayerScreen : Fragment(R.layout.screen_musicplayer) {
    private val binding by viewBinding(ScreenMusicplayerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.nextMusic.setOnClickListener { startMyService(ActionEnum.NEXT) }
        binding.prevMusic.setOnClickListener { startMyService(ActionEnum.PREV) }
        binding.playMusic.setOnClickListener { startMyService(ActionEnum.MANAGE) }
        binding.seekBar.setChangeProgress { }

        MyAppManager.playMusicLiveData.observe(viewLifecycleOwner, playMusicObserver)
        MyAppManager.isPlayingLiveData.observe(viewLifecycleOwner, isPlayingObserver)
        MyAppManager.currentTimeLiveData.observe(viewLifecycleOwner, currentTimeObserver)
    }

    @SuppressLint("SetTextI18n")
    private val playMusicObserver = Observer<MusicData> {
        binding.seekBar.progress = (MyAppManager.currentTime * 100 / MyAppManager.fullTime).toInt()
        val x = it.duration / 1000
        val min = x / 60
        val sec = x % 60
        if (min < 10) {
            if (sec < 10) binding.timeFull.text = "0$min:0$sec"
            else binding.timeFull.text = "0$min:$sec"
        } else {
            if (sec < 10) binding.timeFull.text = "$min:0$sec"
            else binding.timeFull.text = "$min:$sec"
        }
    }

    private val isPlayingObserver = Observer<Boolean> {
        if (it) binding.playMusic.setImageResource(R.drawable.ic_pause)
        else binding.playMusic.setImageResource(R.drawable.ic_play)
    }

    @SuppressLint("SetTextI18n")
    private val currentTimeObserver = Observer<Long> {
        binding.seekBar.progress = (MyAppManager.currentTime * 100 / MyAppManager.fullTime).toInt()
        val time = MyAppManager.currentTime / 1000
        val min = time / 60
        val sec = time % 60
        if (min < 10) {
            if (sec < 10) binding.timeCurrent.text = "0$min:0$sec"
            else binding.timeCurrent.text = "0$min:$sec"
        } else {
            if (sec < 10) binding.timeCurrent.text = "{$min}:0$sec"
            else binding.timeCurrent.text = "{$min}:$sec"
        }
    }

    private fun startMyService(action: ActionEnum) {
        val intent = Intent(requireContext(), MusicService::class.java)
        intent.putExtra("COMMAND", action)
        if (Build.VERSION.SDK_INT >= 26) {
            requireActivity().startForegroundService(intent)
        } else requireActivity().startService(intent)
    }
}