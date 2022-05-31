package uz.gita.musicplayer.mobdev20.presentation.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import uz.gita.musicplayer.mobdev20.data.local.MySharedPreferences
import uz.gita.musicplayer.mobdev20.MainActivity
import uz.gita.musicplayer.mobdev20.R
import uz.gita.musicplayer.mobdev20.data.ActionEnum
import uz.gita.musicplayer.mobdev20.data.MusicData
import uz.gita.musicplayer.mobdev20.presentation.broadcast.BroadcastReceiver
import uz.gita.musicplayer.mobdev20.utils.MyAppManager
import uz.gita.musicplayer.mobdev20.utils.getMusicDataByPosition
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null
    private val CHANEL = "MUSIC"
    private var _mediaPlayer: MediaPlayer? = null
    private val mediaPlayer get() = _mediaPlayer!!
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var job: Job? = null
    private var prevClick = false
    private var nextClick = false
    @Inject
    lateinit var mySharedPreferences: MySharedPreferences
    private val receiver = BroadcastReceiver()


    override fun onCreate() {
        super.onCreate()
        _mediaPlayer = MediaPlayer()
        registerReceiver(receiver, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
        MyAppManager.mediaPlayer = _mediaPlayer
        createChanel()
        startMyService()
    }

    @SuppressLint("ResourceAsColor", "UnspecifiedImmutableFlag")
    private fun startMyService() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, 0)
        val notification: Notification = NotificationCompat.Builder(this, CHANEL)
            .setSmallIcon(R.drawable.ic_music_note_beamed)
            .setContentTitle("Music Player")
            .setCustomBigContentView(createRemoteView())
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
        startForeground(1, notification)
    }

    private fun createRemoteView(): RemoteViews? {
        val view = RemoteViews(this.packageName, R.layout.remote_view)
        val musicData = MyAppManager.cursor?.getMusicDataByPosition(MyAppManager.selectMusicPos)!!
        view.setTextViewText(R.id.textMusicName, musicData.tittle)
        view.setTextViewText(R.id.textArtistName, musicData.artist)
        if (mediaPlayer.isPlaying) {
            view.setImageViewResource(R.id.buttonManage, R.drawable.ic_pause)
        } else view.setImageViewResource(R.id.buttonManage, R.drawable.ic_play)

        view.setOnClickPendingIntent(R.id.buttonPrev, createPendingIntent(ActionEnum.PREV))
        view.setOnClickPendingIntent(R.id.buttonManage, createPendingIntent(ActionEnum.MANAGE))
        view.setOnClickPendingIntent(R.id.buttonNext, createPendingIntent(ActionEnum.NEXT))
        view.setOnClickPendingIntent(R.id.buttonCancel, createPendingIntent(ActionEnum.CANCEL))
        return view
    }

    private fun createChanel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel =
                NotificationChannel("MUSIC", CHANEL, NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(null, null)
            val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createPendingIntent(action: ActionEnum): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("COMMAND", action)
        return PendingIntent.getService(
            this,
            action.position,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMyService()
        val command = intent!!.extras?.getSerializable("COMMAND") as ActionEnum
        doneCommand(command)
        return START_STICKY
    }

    private fun doneCommand(command: ActionEnum) {
        val data: MusicData =
            MyAppManager.cursor?.getMusicDataByPosition(MyAppManager.selectMusicPos)!!
        when (command) {
            ActionEnum.MANAGE -> {
                if (mediaPlayer.isPlaying) doneCommand(ActionEnum.PAUSE)
                else doneCommand(ActionEnum.PLAY)
            }
            ActionEnum.PLAY -> {
                mySharedPreferences.isPlayingMusic = true
                val play = MyAppManager.stateLiveData.value
                if (play == true)
                    mediaPlayer.start()
                if (mediaPlayer.isPlaying)
                    mediaPlayer.stop()

                _mediaPlayer = MediaPlayer.create(this, Uri.fromFile(File(data.data ?: "")))
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener { doneCommand(ActionEnum.NEXT) }
                MyAppManager.fullTime = data.duration
                mediaPlayer.seekTo(MyAppManager.currentTime.toInt())
                job?.cancel()

                job = scope.launch {
                    changeProgress().collectLatest {
                        MyAppManager.currentTime = it
                        MyAppManager.currentTimeLiveData.postValue(it)
                    }
                }

                MyAppManager.isPlayingLiveData.value = true
                MyAppManager.playMusicLiveData.value = data
                startMyService()
            }
            ActionEnum.PREV -> {
                /* MyAppManager.currentTime = 0
                 if (MyAppManager.selectMusicPos == 0) MyAppManager.selectMusicPos =
                     MyAppManager.cursor!!.count - 1
                 else MyAppManager.selectMusicPos--
                 doneCommand(ActionEnum.PLAY)*/
                if (!prevClick) {
                    prevClick = true
                    MyAppManager.currentTime = 0
                    if (MyAppManager.selectMusicPos == 0) MyAppManager.selectMusicPos =
                        MyAppManager.cursor!!.count - 1
                    else MyAppManager.selectMusicPos--
                    if (mediaPlayer.isPlaying) doneCommand(ActionEnum.PLAY)
                    else doneCommand(ActionEnum.PAUSE)
                    scope.launch {
                        delay(300)
                        prevClick = false
                        cancel()
                    }
                }
            }
            ActionEnum.NEXT -> {
                /*MyAppManager.currentTime = 0
                if (MyAppManager.selectMusicPos + 1 == MyAppManager.cursor!!.count) MyAppManager.selectMusicPos =
                    0
                else MyAppManager.selectMusicPos++
                doneCommand(ActionEnum.PLAY)*/
                if (!nextClick) {
                    nextClick = true
                    MyAppManager.currentTime = 0
                    if (MyAppManager.selectMusicPos + 1 == MyAppManager.cursor?.count) MyAppManager.selectMusicPos =
                        0
                    else MyAppManager.selectMusicPos++
                    doneCommand(ActionEnum.PLAY)
                    scope.launch {
                        delay(300)
                        nextClick = false
                        cancel()
                    }
                }
            }
            ActionEnum.PAUSE -> {
                mySharedPreferences.isPlayingMusic = false
                val play = MyAppManager.stateLiveData.value
                if (play == true)
                    mediaPlayer.stop()
                mediaPlayer.stop()
                job?.cancel()
                MyAppManager.isPlayingLiveData.value = false
                startMyService()
            }

            ActionEnum.CANCEL -> {
                mediaPlayer.stop()
                MyAppManager.isPlayingLiveData.value = false
                stopSelf()
            }

            ActionEnum.SEEK -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("TTT", "SEEK")
                    val time = command.position
                    mediaPlayer.seekTo(time.toLong(), MediaPlayer.SEEK_CLOSEST)
                }
            }

        }
    }

    private fun changeProgress(): Flow<Long> = flow {
//        for (i in MyAppManager.currentTime until MyAppManager.fullTime step 1000) {
//            delay(1000)
//            emit(i)
//        }
        while (mediaPlayer.currentPosition < MyAppManager.fullTime) {
            emit(mediaPlayer.currentPosition.toLong())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver.clearAbortBroadcast()
        unregisterReceiver(receiver)
        scope.cancel()
    }
}