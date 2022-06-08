package uz.gita.musicplayer.mobdev20.presentation.ui.screen

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.musicplayer.mobdev20.BuildConfig
import uz.gita.musicplayer.mobdev20.R
import uz.gita.musicplayer.mobdev20.databinding.ScreenSplashBinding
import uz.gita.musicplayer.mobdev20.utils.MyAppManager
import uz.gita.musicplayer.mobdev20.utils.checkPermissions
import uz.gita.musicplayer.mobdev20.utils.getMusicCursor

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreen : Fragment(R.layout.screen_splash) {
    private val binding by viewBinding(ScreenSplashBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textVersion.text =
            resources.getString(R.string.text_splash_music, BuildConfig.VERSION_NAME)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.navigationBarColor =
                ContextCompat.getColor(requireContext(), R.color.purple_200)
        }
        requireActivity().checkPermissions(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_PHONE_STATE
            )
        ) {
            requireContext().getMusicCursor().onEach {
                MyAppManager.noLiveData.value = it.count > 0
                MyAppManager.cursor = it
                delay(2000)
                findNavController().navigate(R.id.action_splashScreen_to_homeScreen)
            }.launchIn(lifecycleScope)
        }
    }

}