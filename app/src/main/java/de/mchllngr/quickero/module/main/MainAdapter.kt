package de.mchllngr.quickero.module.main

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.scopes.ActivityScoped
import de.mchllngr.quickero.R
import de.mchllngr.quickero.databinding.RecyclerViewItemBinding
import javax.inject.Inject

private const val VIEW_TYPE_EMPTY = 1001
private const val VIEW_TYPE_APPLICATION = 1002

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MainItem>() {

    override fun areItemsTheSame(
        oldItem: MainItem,
        newItem: MainItem
    ) = if (oldItem is MainItem.Application && newItem is MainItem.Application) {
        oldItem.packageName == newItem.packageName
    } else {
        oldItem is MainItem.Empty && newItem is MainItem.Empty
    }

    override fun areContentsTheSame(
        oldItem: MainItem,
        newItem: MainItem
    ) = oldItem == newItem
}

@ActivityScoped
class MainAdapter @Inject constructor() : ListAdapter<MainItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        MainItem.Empty -> VIEW_TYPE_APPLICATION
        is MainItem.Application -> VIEW_TYPE_APPLICATION
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (viewType) {
        VIEW_TYPE_EMPTY -> EmptyViewHolder(parent.context)
        VIEW_TYPE_APPLICATION -> ApplicationViewHolder(RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else -> error("Unknown viewType '$viewType'")
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item: MainItem = getItem(position)
        when (holder) {
            is EmptyViewHolder -> Unit
            is ApplicationViewHolder -> holder.setData(item as MainItem.Application)
            else -> error("Unknown ViewHolder '${holder.javaClass}'")
        }
    }

    private class EmptyViewHolder(context: Context) : RecyclerView.ViewHolder(AppCompatTextView(context)) {

        init {
            (itemView as AppCompatTextView).apply {
                layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                gravity = Gravity.CENTER
                setText(R.string.application_list_empty)
            }
        }
    }

    private class ApplicationViewHolder(private val binding: RecyclerViewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(item: MainItem.Application) {
            binding.apply {
                icon.setImageDrawable(item.icon)
                name.text = item.name
            }
        }
    }
}
