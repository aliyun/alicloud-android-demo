package alibaba.httpdns_android_demo.setting

import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.OperationHostItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 预解析域名列表页面和清空指定域名缓存页面的域名列表适配器
 * @author 任伟
 * @date 2024/07/19
 */
class HostListOperationAdapter(val data: List<String>, private val selectedData: List<String>) :
    RecyclerView.Adapter<HostListOperationAdapter.ViewHolder>() {

    /**
     * 域名点击回调
     */
    var onItemClickListener: ((host: String) -> Unit)? = null

    class ViewHolder(val binding: OperationHostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(host: String, isCheck: Boolean) {
            binding.tvHost.text = host
            binding.ivCheck.setImageResource(if (isCheck) R.drawable.httpdns_host_check else R.drawable.httpdns_host_def)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            OperationHostItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindData(data[position], selectedData.contains(data[position]))
        viewHolder.binding.root.setOnClickListener {
            onItemClickListener?.invoke(data[position])
        }
    }

}