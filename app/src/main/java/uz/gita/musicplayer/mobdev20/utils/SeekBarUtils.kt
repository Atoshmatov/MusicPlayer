package uz.gita.musicplayer.mobdev20.utils

import android.widget.SeekBar


fun SeekBar.setChangeProgress(block: (Boolean) -> Unit) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                block(false)
            } else block(true)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            block(false)
        }
    })
}