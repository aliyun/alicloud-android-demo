package alibaba.httpdns_android_demo.best_practice

import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.CaseItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * 最佳实践适配器
 * @author 任伟
 * @date 2024/07/22
 */
class CaseAdapter(val data: List<Pair<Int, String>>) :
    RecyclerView.Adapter<CaseAdapter.ViewHolder>() {

    var onItemClickListener: ((id: Int, position: Int) -> Unit)? = null

    var selectItem: Int = 0

    class ViewHolder(val binding: CaseItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(caseName: String) {
            binding.caseName = caseName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            CaseItemBinding.inflate(
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
        viewHolder.bindData(data[position].second)
        viewHolder.itemView.setBackgroundResource(
            if (position == selectItem) {
                R.drawable.httpdns_case_item_bg_sel
            } else R.drawable.httpdns_case_item_bg_def
        )

        (viewHolder.itemView as TextView).setTextColor(
            if (position == selectItem) {
                ContextCompat.getColor(viewHolder.itemView.context, R.color.httpdns_color_424FF7)
            } else {
                ContextCompat.getColor(viewHolder.itemView.context, R.color.httpdns_color_A7BCCE)
            }
        )

        viewHolder.itemView.setOnClickListener {
            onItemClickListener?.invoke(data[position].first, position)
            val lastSelect = selectItem
            selectItem = position
            notifyItemChanged(lastSelect)
            notifyItemChanged(selectItem)
        }
    }

}