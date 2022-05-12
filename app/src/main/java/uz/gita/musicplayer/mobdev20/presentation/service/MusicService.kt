package uz.gita.musicplayer.mobdev20.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import uz.gita.musicplayer.mobdev20.R

class MusicService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null
    private val CHANEL = "MUSIC"
    private var _mediaPlayer: MediaPlayer? = null
    val mediaPlayer get() = _mediaPlayer
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val job: Job? = null


    override fun onCreate() {
        super.onCreate()
        _mediaPlayer = MediaPlayer()
        createChanel()
        startMyService()
    }

    private fun startMyService() {
        val notification: Notification = NotificationCompat.Builder(this, CHANEL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Music Player")
            .setCustomBigContentView(createRemoteView())
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
    }

    private fun createRemoteView(): RemoteViews? {
        return createRemoteView()
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}