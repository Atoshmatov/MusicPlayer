package uz.gita.musicplayer.mobdev20.presentation.ui.adapter

import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.gita.musicplayer.mobdev20.databinding.ItemMusicBinding
import uz.gita.musicplayer.mobdev20.utils.getMusicDataByPosition

class HomeAdapter(private val scope: CoroutineScope) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    private var clicker = false
    var cursor: Cursor? = null
    private var selectedMusicByPositionListener: ((Int) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (!clicker) {
                    clicker = true
                    selectedMusicByPositionListener?.invoke(absoluteAdapterPosition)
                    scope.launch {
                        delay(400)
                        clicker = false
                        cancel()
                    }
                }
            }
        }

        fun bind() {
            cursor?.getMusicDataByPosition(absoluteAdapterPosition)?.let {
                binding.musicTittle.text = it.tittle
                binding.musicArtis.text = it.artist
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemMusicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = cursor?.count ?: 0

    fun setSelectedMusicByPositionListener(block: (Int) -> Unit) {
        selectedMusicByPositionListener = block
    }
}