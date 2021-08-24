package com.smart.utube.Activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smart.utube.Adapter.SearchQueryAdapter
import com.smart.utube.Network.Repositry
import com.smart.utube.R
import com.smart.utube.SearchData.SearchDataModel
import com.smart.utube.SearchData.SearchViewModel
import com.smart.utube.SearchQuery.Item
import com.smart.utube.ViewModel.YoutubeViewModel
import com.smart.utube.ViewModel.YoutubeViewModelFactory
import com.smart.utube.databinding.ActivityChannelBinding
import com.smart.utube.playlistData.PLayListViewModel
import com.smart.utube.playlistData.PlayList

class ChannelActivity : AppCompatActivity(), SearchQueryAdapter.setOnItemClick {
    private lateinit var binding : ActivityChannelBinding
    lateinit var toolbar: Toolbar
    lateinit var manager : LinearLayoutManager
    lateinit var viewmodel : YoutubeViewModel
    lateinit var itemList : ArrayList<Item>
    lateinit var adapter : ArrayAdapter<String>
    lateinit var searchAdapter : SearchQueryAdapter
    lateinit var  playlistViewModel : PLayListViewModel
    lateinit var playlist : ArrayList<PlayList>
    var searchList = ArrayList<SearchDataModel>()
    var list = ArrayList<String>()


    var currentItem : Int = 0
    var totalItem : Int = 0
    var scrollOutItem : Int = 0
    lateinit var nextPageId : String
    lateinit var channelId : String
    var isScrolling = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        channelId = intent.getStringExtra("channel_ID")!!

        playlistViewModel = ViewModelProvider(this).get(PLayListViewModel::class.java)
        getdata()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        binding.recyclerViewChannel.setHasFixedSize(true)
        manager = LinearLayoutManager(this)
        binding.recyclerViewChannel.layoutManager = manager


        binding.recyclerViewChannel.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                isScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                currentItem = manager.childCount
                scrollOutItem = manager.findFirstVisibleItemPosition()
                totalItem = manager.itemCount

                if (isScrolling && currentItem + scrollOutItem == totalItem){
                    binding.progressbarSearch.visibility = View.VISIBLE
                    Handler().postDelayed(Runnable { LoadMoreData(nextPageId, channelId) },2000)
                }

                isScrolling = false
            }
        })


        getList()


    }

    private fun LoadMoreData(nextToken : String,channelid : String) {
        val repositry = Repositry()
        val youtubeViewModelFactory= YoutubeViewModelFactory(repositry)

        viewmodel = YoutubeViewModel(repositry)
        viewmodel = ViewModelProvider(this,youtubeViewModelFactory) [YoutubeViewModel::class.java]
        viewmodel.getChannelList(nextToken,channelid)
        viewmodel.searchList.observe(this,{
            nextPageId = it.nextPageToken

            itemList = ArrayList()
            Log.d("SearchList", ""+it.items.size)

            for (i in it.items){
                itemList.add(Item(i.etag,i.id,i.kind,i.snippet))
                Log.d("SearchList", ""+itemList.get(0).snippet.title)
            }

            binding.progressbarSearch.visibility = View.GONE
            searchAdapter = SearchQueryAdapter(this,itemList,this)
            binding.recyclerViewChannel.adapter  =  searchAdapter

        })


    }

    private fun getList() {
        val repositry = Repositry()
        val youtubeViewModelFactory= YoutubeViewModelFactory(repositry)

        viewmodel = YoutubeViewModel(repositry)
        viewmodel = ViewModelProvider(this,youtubeViewModelFactory) [YoutubeViewModel::class.java]
        viewmodel.getChannelList(channelId, "")
        viewmodel.searchList.observe(this,{
            nextPageId = it.nextPageToken
            itemList = ArrayList()

            for (i in it.items){
                itemList.add(Item(i.etag,i.id,i.kind,i.snippet))
                Log.d("Search", ""+itemList.get(0).snippet.title)
            }

            binding.progressbarSearch.visibility = View.GONE
            searchAdapter = SearchQueryAdapter(this,itemList,this)
            binding.recyclerViewChannel.adapter  =  searchAdapter
        })

    }

    override fun OnitemClick(position: Int) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("position",position)
        intent.putExtra("VideoId",itemList.get(position).id.videoId)
        intent.putExtra("title",itemList.get(position).snippet.title)
        intent.putExtra("description",itemList.get(position).snippet.description)
        intent.putExtra("publishDate",itemList.get(position).snippet.publishedAt)
        intent.putExtra("thumbnail",itemList.get(position).snippet.thumbnails.high.url)
        startActivity(intent)
    }

    override fun channelIDClick(position: Int) {

    }

    override fun addPlayList(position: Int) {
        playListDialog(position)
    }

    override fun onItemShare(position: Int) {
        val shareApp = Intent(Intent.ACTION_SEND);
        shareApp.setType("text/plan");
        shareApp.putExtra(Intent.EXTRA_SUBJECT,"");
        shareApp.putExtra(Intent.EXTRA_TEXT,"https://youtu.be/"+itemList.get(position).id.videoId);
        startActivity(Intent.createChooser(shareApp,"Share By"))
    }

    fun getdata(){
        playlist = ArrayList()
        playlistViewModel.readAllData.observe(this, Observer {
            playlist.addAll(it)
        })
    }

    private fun addSongs(position: Int) {

        var found = false
        if (playlist.size > 0) {
            for (i in playlist){
                if (i.title.equals(itemList.get(position).snippet.title)){
                    found = true
                }
            }

            if (!found){
                val playListItem = PlayList(
                        0,
                        itemList.get(position).snippet.title,
                        itemList.get(position).snippet.thumbnails.high.url,
                        itemList.get(position).snippet.description,
                        itemList.get(position).snippet.publishedAt,
                        "noDuration",
                        itemList.get(position).id.videoId,
                        "Songs"
                )
                playlistViewModel.addPlaylist(playListItem)
            }

        }else{
            val playListItem = PlayList(
                    0,
                    itemList.get(position).snippet.title,
                    itemList.get(position).snippet.thumbnails.high.url,
                    itemList.get(position).snippet.description,
                    itemList.get(position).snippet.publishedAt,
                    "noDuration",
                    itemList.get(position).id.videoId,
                    "Songs"
            )
            playlistViewModel.addPlaylist(playListItem)
            Toast.makeText(this,"song add", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addImportant(position: Int) {
        if (playlist.size > 0) {
            for (i in playlist){

                if (i.title.equals(itemList.get(position).snippet.title)){
                    val playListItem = PlayList(
                            0,
                            itemList.get(position).snippet.title,
                            itemList.get(position).snippet.thumbnails.high.url,
                            itemList.get(position).snippet.description,
                            itemList.get(position).snippet.publishedAt,
                            "noDuration",
                            itemList.get(position).id.videoId,
                            "Songs"
                    )
                    playlistViewModel.addPlaylist(playListItem)
                    Toast.makeText(this,"song add", Toast.LENGTH_SHORT).show()
                }

            }

        }else{
            val playListItem = PlayList(
                    0,
                    itemList.get(position).snippet.title,
                    itemList.get(position).snippet.thumbnails.high.url,
                    itemList.get(position).snippet.description,
                    itemList.get(position).snippet.publishedAt,
                    "noDuration",
                    itemList.get(position).id.videoId,
                    "Important"
            )
            playlistViewModel.addPlaylist(playListItem)
        }
    }

    private fun playListDialog(position: Int){
        val alert = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.playlist_dialog_list, null)
        val important = view.findViewById<LinearLayout>(R.id.important_playlist_dialog)

        val alertDialog = alert.create()
        alertDialog.setView(view)
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.show()

        important.setOnClickListener{
            addImportant(position)
        }

    }
}