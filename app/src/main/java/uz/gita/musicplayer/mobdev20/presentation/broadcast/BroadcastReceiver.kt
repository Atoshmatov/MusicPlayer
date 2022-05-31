package uz.gita.musicplayer.mobdev20.presentation.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.gita.musicplayer.mobdev20.data.local.MySharedPreferences
import uz.gita.musicplayer.mobdev20.data.ActionEnum
import uz.gita.musicplayer.mobdev20.presentation.service.MusicService
import uz.gita.musicplayer.mobdev20.utils.MyAppManager
import javax.inject.Inject

@AndroidEntryPoint
class BroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var mySharedPreferences: MySharedPreferences

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                telephonyManager.registerTelephonyCallback(
                    context.mainExecutor,
                    object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                        override fun onCallStateChanged(state: Int) {
                            Timber.d("TTT onCallStateChanged state: $state")
                            when (state) {
                                TelephonyManager.CALL_STATE_RINGING -> {
                                    Timber.d("TTT onCallStateChanged CALL_STATE_RINGING")
                                    MyAppManager.phoneStata = true
                                    if (mySharedPreferences.isPlayingMusic) startMyService(
                                        ActionEnum.PAUSE,
                                        context
                                    )
                                }
                                TelephonyManager.CALL_STATE_IDLE -> {
                                    if (MyAppManager.phoneStata && !mySharedPreferences.isPlayingMusic) {
                                        startMyService(ActionEnum.PLAY, context)
                                        MyAppManager.phoneStata = false
                                    }
                                    Timber.d("TTT onCallStateChanged CALL_STATE_IDLE")
                                }
                                TelephonyManager.CALL_STATE_OFFHOOK -> {
                                    Timber.d("TTT onCallStateChanged CALL_STATE_OFFHOOK")
                                    MyAppManager.phoneStata = true
                                    if (mySharedPreferences.isPlayingMusic) startMyService(
                                        ActionEnum.PAUSE,
                                        context
                                    )
                                }
                            }
                        }
                    })
            } else {
                telephonyManager.listen(object : PhoneStateListener() {
                    @Deprecated("Deprecated in Java")
                    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                        Timber.d("TTT onCallStateChanged state: $state")
                        when (state) {
                            TelephonyManager.CALL_STATE_RINGING -> {
                                Timber.d("TTT onCallStateChanged CALL_STATE_RINGING")
                                MyAppManager.phoneStata = true
                                if (mySharedPreferences.isPlayingMusic) startMyService(
                                    ActionEnum.PAUSE,
                                    context
                                )
                            }
                            TelephonyManager.CALL_STATE_IDLE -> {
                                if (MyAppManager.phoneStata && !mySharedPreferences.isPlayingMusic && MyAppManager.selectMusicPos != -1) {
                                    startMyService(ActionEnum.PLAY, context)
                                    MyAppManager.phoneStata = false
                                }
                                Timber.d("TTT onCallStateChanged CALL_STATE_IDLE")
                            }
                            TelephonyManager.CALL_STATE_OFFHOOK -> {
                                Timber.d("TTT onCallStateChanged CALL_STATE_OFFHOOK")
                                MyAppManager.phoneStata = true
                                if (mySharedPreferences.isPlayingMusic) startMyService(
                                    ActionEnum.PAUSE,
                                    context
                                )
                            }
                        }
                    }
                }, PhoneStateListener.LISTEN_CALL_STATE)
            }
        }
    }

    private fun startMyService(command: ActionEnum, context: Context) {
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra("COMMAND", command)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent)
        else context.startService(intent)
    }
}