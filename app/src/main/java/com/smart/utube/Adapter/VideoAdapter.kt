package com.smart.utube.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smart.utube.DataClasses.Item
import com.smart.utube.R
import com.squareup.picasso.Picasso

class VideoAdapter(val context : Context, val mlist : ArrayList<Item>, val mlistener : setOnItemClick) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {


    inner class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.title_video)
        val more = itemView.findViewById<ImageView>(R.id.more_video)
        val share = itemView.findViewById<ImageView>(R.id.share_video)
        val duration = itemView.findViewById<TextView>(R.id.duration_video_layout)
        val thubnails = itemView.findViewById<ImageView>(R.id.thubnail_video)
        val publistDetails = itemView.findViewById<TextView>(R.id.PublishDate_video)
        val rootlayout = itemView.findViewById<LinearLayout>(R.id.root_video)

        init {
            rootlayout.setOnClickListener{
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    mlistener.OnitemClick(position)
                }
            }

            more.setOnClickListener{
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    mlistener.addPlayList(position)
                }
            }

            share.setOnClickListener{
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    mlistener.onItemShare(position)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view  = LayoutInflater.from(parent.context).inflate(R.layout.video_list_layout, null)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        view.layoutParams = layoutParams
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(mlist.get(position).snippet.title)
        var duration = mlist.get(position).contentDetails.duration.removeRange(0,2)
        duration = duration.replace("M","m")
        duration = duration.replace("S","s")
        holder.duration.setText(duration)
        holder.publistDetails.setText(mlist.get(position).snippet.channelTitle+" | "+getCountView(mlist.get(position).statistics.viewCount)+" | "+mlist.get(position).snippet.publishedAt)
        Picasso.get().load(mlist.get(position).snippet.thumbnails.high.url).placeholder(R.drawable.utube_appbar).into(holder.thubnails)

    }

    private fun getCountView(viewCount: String) : String {
        var count = "0"

        if (viewCount.length > 5){
            if(viewCount.length > 7) {
                count = viewCount.substring(0, 2) + "M"
            }else if (viewCount.length <= 7){
                count = viewCount.substring(0, 1) +"."+ viewCount.substring(1,2)+"M"
            }else if (viewCount.length <=5){
                count = viewCount + "K"
            }
        }else{
            if (viewCount.length <=5){
                count = viewCount  + "K"
            }
        }

        return  count
    }

    override fun getItemCount(): Int {
        return mlist.size
    }

    interface setOnItemClick {
        fun OnitemClick(position : Int)
        fun addPlayList(position: Int)
        fun onItemShare(position: Int)
    }

}