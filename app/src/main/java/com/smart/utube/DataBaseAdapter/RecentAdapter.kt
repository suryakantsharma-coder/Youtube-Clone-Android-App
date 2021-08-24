package com.smart.utube.DataBaseAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smart.utube.Data.Recent
import com.smart.utube.R
import com.squareup.picasso.Picasso
import kotlin.math.min

class RecentAdapter(val mlistener : OnRecentItemDelete) : RecyclerView.Adapter<RecentAdapter.ViewHolder>() {

    private var recentList = emptyList<Recent>()

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.title_video)
        val delete = itemView.findViewById<ImageView>(R.id.delete_video)
        val thubnails = itemView.findViewById<ImageView>(R.id.thubnail_video)
        val publistDetails = itemView.findViewById<TextView>(R.id.PublishDate_video)
        val rootlayout = itemView.findViewById<LinearLayout>(R.id.root_video)
        val watchTime = itemView.findViewById<TextView>(R.id.watchTime)

        init {
            delete.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    mlistener.RecentDelete(position)
                }
            }

            rootlayout.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    mlistener.onItemPlayClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.recent_layout, null)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        view.layoutParams = layoutParams
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(recentList.get(position).title)
        holder.publistDetails.setText(recentList.get(position).publish)
        val minutes = recentList.get(position).time / 60
        holder.watchTime.setText(""+ minutes.toInt()+"m")
        Picasso.get().load(recentList.get(position).thumbnail).into(holder.thubnails)
    }

    override fun getItemCount(): Int {
        return recentList.size
    }

    fun addData(recent : List<Recent>){
        this.recentList = recent
        notifyDataSetChanged()
    }

    interface OnRecentItemDelete {
        fun RecentDelete(position : Int)
        fun onItemPlayClick (position: Int)
    }

}