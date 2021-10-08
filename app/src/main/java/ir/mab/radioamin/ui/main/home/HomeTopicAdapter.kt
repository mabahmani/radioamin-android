package ir.mab.radioamin.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemHomeTopicBinding
import ir.mab.radioamin.vo.enums.TopicType
import ir.mab.radioamin.vo.res.Topic

class HomeTopicAdapter(private val list: List<Topic>): RecyclerView.Adapter<HomeTopicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHomeTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title = list[position].title
        when (list[position].topicType){
            TopicType.MUSIC ->{
                holder.binding.list.layoutManager = LinearLayoutManager(holder.binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                holder.binding.list.adapter = HomeTopicMusicAdapter(list[position].musics)
            }

            TopicType.QUICK_PICK->{
                holder.binding.list.layoutManager = GridLayoutManager(holder.binding.root.context, 3, LinearLayoutManager.HORIZONTAL, false)
                holder.binding.list.adapter = HomeTopicQuickPickAdapter(list[position].musics)
            }

            TopicType.ALBUM->{
                holder.binding.list.layoutManager = LinearLayoutManager(holder.binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                holder.binding.list.adapter = HomeTopicAlbumAdapter(list[position].albums)
            }

            TopicType.MUSIC_VIDEO->{
                holder.binding.list.layoutManager = LinearLayoutManager(holder.binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                holder.binding.list.adapter = HomeTopicMusicVideoAdapter(list[position].musics)
            }

            TopicType.PLAYLIST->{
                holder.binding.list.layoutManager = LinearLayoutManager(holder.binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                holder.binding.list.adapter = HomeTopicPlaylistAdapter(list[position].playlists)
            }

            TopicType.SINGER->{
                holder.binding.list.layoutManager = LinearLayoutManager(holder.binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                holder.binding.list.adapter = HomeTopicSingerAdapter(list[position].singers)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView:ItemHomeTopicBinding): RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
        init {
            itemView.executePendingBindings()
        }
    }
}