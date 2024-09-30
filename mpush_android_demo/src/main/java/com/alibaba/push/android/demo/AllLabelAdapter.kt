package com.alibaba.push.android.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.push.android.demo.databinding.LabelItemBinding

class AllLabelAdapter(val labels:MutableList<String>):RecyclerView.Adapter<AllLabelAdapter.ViewHolder>() {

    var deleteLabelCallback: ((String) -> Unit)? = null

    class ViewHolder(val binding: LabelItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int) = ViewHolder(
        LabelItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount() = labels.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvLabel.text = labels[position]
        holder.itemView.background =
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.push_label_bg)
        holder.binding.ivDelete.setImageResource(R.drawable.push_delete)
        holder.binding.tvLabel.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                R.color.push_color_424FF7
            )
        )
        holder.itemView.setOnClickListener {
            deleteLabelCallback?.invoke(labels[position])
        }
    }

}