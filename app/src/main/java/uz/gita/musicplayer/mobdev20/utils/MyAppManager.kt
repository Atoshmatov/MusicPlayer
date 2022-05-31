package uz.gita.musicplayer.mobdev20.utils

import android.database.Cursor
import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import uz.gita.musicplayer.mobdev20.data.MusicData

object MyAppManager {

    var selectMusicPos: Int = 1
    var cursor: Cursor? = null
    var mediaPlayer: MediaPlayer? = null


    var currentTime: Long = 0L
    var fullTime: Long = 0L
    var phoneStata = false


    val currentTimeLiveData = MutableLiveData<Long>()

    val noLiveData = MutableLiveData<Boolean>()

    val playMusicLiveData = MutableLiveData<MusicData>()
    val isPlayingLiveData = MutableLiveData<Boolean>()

    val stateLiveData = MutableLiveData<Boolean>()
}