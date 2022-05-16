package uz.gita.musicplayer.mobdev20.utils

import android.database.Cursor
import androidx.lifecycle.MutableLiveData
import uz.gita.musicplayer.mobdev20.data.MusicData

object MyAppManager {

    var selectMusicPos: Int = 1
    var cursor: Cursor? = null


    var currentTime: Long = 0L
    var fullTime: Long = 0L


    val currentTimeLiveData = MutableLiveData<Long>()

    val playMusicLiveData = MutableLiveData<MusicData>()
    val isPlayingLiveData = MutableLiveData<Boolean>()
}