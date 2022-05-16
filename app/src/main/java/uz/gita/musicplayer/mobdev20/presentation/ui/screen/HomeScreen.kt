package uz.gita.musicplayer.mobdev20.presentation.ui.screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.musicplayer.mobdev20.R
import uz.gita.musicplayer.mobdev20.data.ActionEnum
import uz.gita.musicplayer.mobdev20.data.MusicData
import uz.gita.musicplayer.mobdev20.databinding.ScreenHomeBinding
import uz.gita.musicplayer.mobdev20.presentation.service.MusicService
import uz.gita.musicplayer.mobdev20.presentation.ui.adapter.HomeAdapter
import uz.gita.musicplayer.mobdev20.utils.MyAppManager

@AndroidEntryPoint
class HomeScreen:Fragment(R.layout.screen_home) {
    private val binding by viewBinding(ScreenHomeBinding::bind)
    private val adapter = HomeAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView2.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView2.adapter = adapter
        adapter.cursor = MyAppManager.cursor

        binding.constraintLayout.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreen_to_musicPlayerScreen)
        }
        binding.next.setOnClickListener { startMyService(ActionEnum.NEXT) }
        binding.prev.setOnClickListener { startMyService(ActionEnum.PREV) }
        binding.play.setOnClickListener { startMyService(ActionEnum.MANAGE) }
        adapter.setSelectedMusicByPositionListener {
            MyAppManager.selectMusicPos = it
            startMyService(ActionEnum.PLAY)
        }

        MyAppManager.playMusicLiveData.observe(viewLifecycleOwner, playMusicObserver)
        MyAppManager.isPlayingLiveData.observe(viewLifecycleOwner, isPlayingObserver)
    }

    private fun startMyService(action: ActionEnum) {
        val intent = Intent(requireContext(), MusicService::class.java)
        intent.putExtra("COMMAND", action)
        if (Build.VERSION.SDK_INT >= 26) {
            requireActivity().startForegroundService(intent)
        } else requireActivity().startService(intent)
    }

    private val playMusicObserver = Observer<MusicData> { data ->
        binding.apply {
            musicPlayTittle.text = data.tittle
            artis.text = data.artist
        }
    }

    private val isPlayingObserver = Observer<Boolean> {
        if (it) binding.play.setImageResource(R.drawable.ic_pause)
        else binding.play.setImageResource(R.drawable.ic_play)
    }
}