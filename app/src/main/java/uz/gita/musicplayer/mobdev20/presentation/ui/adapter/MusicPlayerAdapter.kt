package uz.gita.musicplayer.mobdev20.presentation.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.gita.musicplayer.mobdev20.data.MusicData

class MusicPlayerAdapter : ListAdapter<MusicData, MusicPlayerAdapter.ViewHolder>(MusicDataDiffUtils) {

    object MusicDataDiffUtils : DiffUtil.ItemCallback<MusicData>() {
        override fun areItemsTheSame(oldItem: MusicData, newItem: MusicData): Boolean {
            TODO("Not yet implemented")
        }

        override fun areContentsTheSame(oldItem: MusicData, newItem: MusicData): Boolean {
            TODO("Not yet implemented")
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}