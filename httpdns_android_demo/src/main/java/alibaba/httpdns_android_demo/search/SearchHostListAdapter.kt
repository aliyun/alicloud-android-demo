package alibaba.httpdns_android_demo.search

import alibaba.httpdns_android_demo.databinding.SearchHostItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

/**
 * 搜索页面 搜索历史和控制台域名列表adapter
 * @author 任伟
 * @data 2024/07/19
 */
class SearchHostListAdapter(val data:List<String>):RecyclerView.Adapter<SearchHostListAdapter.ViewHolder>() {

    /**
     * 域名删除回调
     */
    var onItemDeleteListener:((host:String,position:Int) ->Unit)? = null

    /**
     * 域名点击回调
     */
    var onItemClickListener:((host:String) ->Unit)? = null

    /**
     * 是否隐藏右侧删除按钮
     */
    var hideClose = false

    class ViewHolder(val binding: SearchHostItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bindData(host:String){
            binding.tvHost.text = host
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(SearchHostItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindData(data[position])
        viewHolder.binding.ivClose.isVisible = !hideClose
        viewHolder.binding.ivClose.setOnClickListener {
            onItemDeleteListener?.invoke(data[position] , position)
        }
        viewHolder.binding.root.setOnClickListener {
            onItemClickListener?.invoke(data[position])
        }
    }

}