package de.mchllngr.quickero.util.applicationlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.mchllngr.quickero.databinding.ApplicationListItemBinding
import de.mchllngr.quickero.repository.application.Application
import de.mchllngr.quickero.repository.application.PackageName

class ApplicationListAdapter(
    private val items: List<Application>,
    private val onSelected: (PackageName) -> Unit
) : RecyclerView.Adapter<ApplicationListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(ApplicationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.setData(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ApplicationListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(item: Application) {
            binding.icon.setImageDrawable(item.icon)
            binding.name.text = item.name
            itemView.setOnClickListener { onSelected(item.packageName) }
        }
    }
}
