package de.mchllngr.quickopen.util.dialog

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.mchllngr.quickopen.R

data class ApplicationItem(
    val name: String,
    val icon: Drawable,
    val onClick: () -> Unit
)

class ApplicationListAdapter(private val items: List<ApplicationItem>) : RecyclerView.Adapter<ApplicationListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false))

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = items[position]
        holder.icon.setImageDrawable(item.icon)
        holder.name.text = item.name
        holder.itemView.setOnClickListener { item.onClick() }
    }

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val icon: ImageView = itemView.findViewById(R.id.icon)
        val name: TextView = itemView.findViewById(R.id.name)
        private val handle: ImageButton = itemView.findViewById(R.id.handle)

        init {
            handle.visibility = View.GONE
        }
    }
}
