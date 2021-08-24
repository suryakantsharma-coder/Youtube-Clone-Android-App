package com.smart.utube.Adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivities
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.smart.utube.Activity.ChannelActivity
import com.smart.utube.R
import com.smart.utube.SearchQuery.Item
import com.squareup.picasso.Picasso
import java.sql.Time
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchQueryAdapter(val context : Context, val mlist : ArrayList<Item>, val mlistener : setOnItemClick) : RecyclerView.Adapter<SearchQueryAdapter.ViewHolder>() {


    inner class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.title_video)
        val more = itemView.findViewById<ImageView>(R.id.more_video)
        val share  = itemView.findViewById<ImageView>(R.id.share_video)
        val thubnails = itemView.findViewById<ImageView>(R.id.thubnail_video)
        val publistDetails = itemView.findViewById<TextView>(R.id.PublishDate_video)
        val duration = itemView.findViewById<TextView>(R.id.duration_video_layout)
        val rootlayout = itemView.findViewById<LinearLayout>(R.id.root_video)
        val logo = itemView.findViewById<ImageView>(R.id.channel_logo_video)

        init {
            rootlayout.setOnClickListener{
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    mlistener.OnitemClick(position)
                }
            }

            logo.setOnClickListener{
                val position = adapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    mlistener.channelIDClick(position)
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
        holder.duration.visibility = View.GONE
        val date = mlist.get(position).snippet.publishTime.replace("T", " Time ")
        val final = date.replace("Z", "")
        holder.publistDetails.setText(mlist.get(position).snippet.channelTitle+" | Date "+final)
        Picasso.get().load(mlist.get(position).snippet.thumbnails.high.url).placeholder(R.drawable.utube_appbar).into(holder.thubnails)
        if(mlist.get(position).snippet.liveBroadcastContent.equals("upcoming")){
            holder.title.setText("Channel : "+mlist.get(position).snippet.title)
            holder.publistDetails.setText(mlist.get(position).snippet.channelTitle+" |  Date "+final)
            holder.rootlayout.isEnabled = false
            holder.thubnails.setOnClickListener{
                val intent = Intent(context, ChannelActivity::class.java)
                intent.putExtra("channel_ID",mlist.get(position).id.channelId)
                context.startActivity(intent)
            }
        } else {
            holder.rootlayout.isEnabled = true
        }

    }


    private fun getMonth(date: String) : String {

        var monthNum : String = date.substring(4,7)
        val date = date.substring(8,10)
        //val year = date.substring(0,4)

        var month = "null"

        when(monthNum){
            "01"-> {month = "Jan"}
            "02"-> {month = "Feb"}
            "03"-> {month = "Mar"}
            "04"-> {month = "Apr"}
            "05"-> {month = "May"}
            "06"-> {month = "Jun"}
            "07"-> {month = "Jul"}
            "08"-> {month = "Aug"}
            "09"-> {month = "Sep"}
            "10"-> {month = "Oct"}
            "11"-> {month = "Nov"}
            "12"-> {month = "Dec"}

        }

        val finaldate = date+" "+monthNum+" "
        return finaldate
    }

    override fun getItemCount(): Int {
        return mlist.size
    }

    interface setOnItemClick {
        fun OnitemClick(position : Int)
        fun channelIDClick(position : Int)
        fun addPlayList(position: Int)
        fun onItemShare(position: Int)
    }

}