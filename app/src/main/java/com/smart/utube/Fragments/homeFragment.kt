package com.smart.utube.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.smart.utube.Activity.PlayerActivity
import com.smart.utube.Adapter.RelatedVideoAdapter
import com.smart.utube.DataBaseAdapter.RelatedVideoDatabaseAdapter
import com.smart.utube.HomeDatabase.HomeDataset
import com.smart.utube.HomeDatabase.HomeViewModel
import com.smart.utube.Network.Repositry
import com.smart.utube.R
import com.smart.utube.RelatedVideoDataset.Item
import com.smart.utube.ViewModel.YoutubeViewModel
import com.smart.utube.ViewModel.YoutubeViewModelFactory
import com.smart.utube.playlistData.PLayListViewModel
import com.smart.utube.playlistData.PlayList
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class homeFragment : Fragment(), RelatedVideoAdapter.setOnItemClick, RelatedVideoDatabaseAdapter.setOnItemClick {
    lateinit var recyclerView: RecyclerView
    lateinit var viewmodel : YoutubeViewModel
    lateinit var progressbar : LinearLayout
    lateinit var itemList : ArrayList<Item>
    lateinit var adapter : RelatedVideoAdapter
    lateinit var swipeRefresh : SwipeRefreshLayout
    lateinit var adapter2 : RelatedVideoDatabaseAdapter
    var offline : Boolean = false
    var onlyOne : Boolean = true
    //playList
    lateinit var  playlistViewModel : PLayListViewModel
    lateinit var playlist : ArrayList<PlayList>
    var videoID : String? = null

    //Home Database
    lateinit var homeViewModel : HomeViewModel
    lateinit var homeList : ArrayList<HomeDataset>

    var currentItem : Int = 0
    var totalItem : Int = 0
    var scrollOutItem : Int = 0
    lateinit var nextPageToken : String
    lateinit var search : String
    lateinit var manager : LinearLayoutManager
    var isScrolling = false


    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        progressbar = view.findViewById(R.id.progrssBar_home)
        swipeRefresh = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView_home_fragment)
        recyclerView.setHasFixedSize(true)
        manager = LinearLayoutManager(context)
        recyclerView.layoutManager = manager

        //database list init
        homeList = ArrayList()

        //shared preferences
        val sharedPreferences = context!!.getSharedPreferences("relatedVideoId", AppCompatActivity.MODE_PRIVATE)
        videoID = sharedPreferences.getString("videoId", "Ks-_Mh1QhMc").toString()



        playlistViewModel = ViewModelProvider(this).get(PLayListViewModel::class.java)
        getdata()

//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                isScrolling = true
//            }
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                currentItem = manager.childCount
//                scrollOutItem = manager.findFirstVisibleItemPosition()
//                totalItem = manager.itemCount
//
//                if (isScrolling && currentItem + scrollOutItem == totalItem){
//                    progressbar.visibility = View.VISIBLE
//                    val sharedPreferences = requireContext().getSharedPreferences("homeNextPageToken", AppCompatActivity.MODE_PRIVATE)
//                    val nextToken = sharedPreferences.getString("NextToken","No More Result")
//                    if (!nextToken.equals("")) { Handler().postDelayed(Runnable { LoadMoreData(nextToken!!, videoID!!) }, 2000) } else{ Toast.makeText(requireContext(),"No More Result",Toast.LENGTH_SHORT).show() }
//                }
//
//                isScrolling = false
//            }
//        })

        Log.d("videoIdES", "" + videoID)


        Log.d("databaseTest", getDatbaseListCount().toString())

        fetchFromDatabase()

        swipeRefresh.setOnRefreshListener {
            homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
            homeViewModel.deleteAll()
            Handler().postDelayed(Runnable {
                getList(videoID!!)
                swipeRefresh.isRefreshing = false
            }, 4000)


        }

        return view


    }

    private fun LoadMoreData(nextToken: String, videoid: String) {

        Log.d("tryingfortoken", nextToken + videoid)

        val sharedPreferences = requireContext().getSharedPreferences("homeNextPageToken", AppCompatActivity.MODE_PRIVATE)
        val sharedPref : SharedPreferences.Editor = sharedPreferences.edit()

        val repositry = Repositry()
        val youtubeViewModelFactory= YoutubeViewModelFactory(repositry)

        viewmodel = YoutubeViewModel(repositry)
        viewmodel = ViewModelProvider(this, youtubeViewModelFactory)[YoutubeViewModel::class.java]
        viewmodel.getNextPageRealatedVideos(nextToken, videoid)
        viewmodel.listofRelatedVideos.observe(this, {
            sharedPref.putString("NextToken", it.nextPageToken)
            sharedPref.apply()
            nextPageToken = it.nextPageToken

            itemList = ArrayList()
            Log.d("SearchList", "" + it.items.size)

            for (i in it.items) {
                itemList.add(Item(i.etag, i.id, i.kind, i.snippet))
                Log.d("SearchList", "" + itemList.get(0).snippet.title)
            }

            progressbar.visibility = View.GONE
            adapter = RelatedVideoAdapter(requireContext(), itemList, this)
            recyclerView.adapter = adapter

        })


    }


    fun getList(videoid: String) {

        val sharedPreferences = requireContext().getSharedPreferences("homeNextPageToken", AppCompatActivity.MODE_PRIVATE)
        val sharedPref : SharedPreferences.Editor = sharedPreferences.edit()

        val repositry = Repositry()
        val YoutubeViewModelFactory = YoutubeViewModelFactory(repositry)
        viewmodel = YoutubeViewModel(repositry)
        viewmodel = ViewModelProvider(this, YoutubeViewModelFactory)[YoutubeViewModel::class.java]
        viewmodel.getRelatedVideos(videoid)
        Log.d("ideses", videoid)
        viewmodel.listofRelatedVideos.observe(viewLifecycleOwner, {

            sharedPref.putString("NextToken", it.nextPageToken)
            sharedPref.apply()
            this.nextPageToken = it.nextPageToken

            itemList = ArrayList<Item>()



            if (onlyOne) {

//                itemList.clear()
                homeList.clear()

                for (i in it.items) {
                    Log.d("dataMonitor", i.snippet.title + "\t" + "\t" + i.snippet.publishedAt + "\t" + i.snippet.channelTitle + "\t" + i.snippet.thumbnails.high.url + "\t" + i.id.videoId + "\n\n\n\n")
                    itemList.add(Item(i.etag, i.id, i.kind, i.snippet))
                    homeViewModel.addVideo(HomeDataset(0, i.snippet.title, i.snippet.description, i.snippet.publishedAt, i.snippet.channelTitle, i.snippet.thumbnails.high.url, i.id.videoId))
                }


                Log.d("databaseTest", homeList.size.toString() + "\t" + itemList.size.toString())

                cleanDataToBeShow()

                Handler().postDelayed(Runnable {
                    progressbar.visibility = View.GONE
                    adapter = RelatedVideoAdapter(requireContext(), itemList, this)
                    recyclerView.adapter = adapter
                },4000)

                onlyOne = false
            }
        })

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                homeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun OnitemClick(position: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("position", position)
//        intent.putExtra("VideoId",itemList.get(position).id.videoId)
//        intent.putExtra("title",itemList.get(position).snippet.title)
//        intent.putExtra("description",itemList.get(position).snippet.description)
//        intent.putExtra("publishDate",itemList.get(position).snippet.publishedAt)
//        intent.putExtra("thumbnail",itemList.get(position).snippet.thumbnails.high.url)
        intent.putExtra("VideoId", homeList.get(position).videoId)
        intent.putExtra("title", homeList.get(position).title)
        intent.putExtra("description", homeList.get(position).description)
        intent.putExtra("publishDate", homeList.get(position).publishedAt)
        intent.putExtra("thumbnail", homeList.get(position).thumbnails)
        startActivity(intent)
    }

    override fun addPlayList(position: Int) {
        playListDialog(position)
    }

    override fun onItemShare(position: Int) {
        val shareApp = Intent(Intent.ACTION_SEND);
        shareApp.setType("text/plan");
        shareApp.putExtra(Intent.EXTRA_SUBJECT, "");
        shareApp.putExtra(Intent.EXTRA_TEXT, "https://youtu.be/" + itemList.get(position).id.videoId);
        startActivity(Intent.createChooser(shareApp, "Share By"))
    }

    fun getdata(){
        playlist = ArrayList()
        playlistViewModel.readAllData.observe(viewLifecycleOwner, Observer {
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
                Toast.makeText(requireContext(), "Add In Playlist", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "Add In Playlist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addSongsDatabase(position: Int) {

        var found = false
        if (playlist.size > 0) {
            for (i in playlist){
                if (i.title.equals(homeList.get(position).title)){
                    found = true
                }
            }

            if (!found){
                val playListItem = PlayList(
                        0,
                        homeList.get(position).title,
                        homeList.get(position).thumbnails,
                        homeList.get(position).description,
                        homeList.get(position).publishedAt,
                        "noDuration",
                        homeList.get(position).videoId,
                        "Songs"
                )
                playlistViewModel.addPlaylist(playListItem)
                Toast.makeText(requireContext(), "Add In Watch Later", Toast.LENGTH_SHORT).show()
            }

        }else{
            val playListItem = PlayList(
                    0,
                    homeList.get(position).title,
                    homeList.get(position).thumbnails,
                    homeList.get(position).description,
                    homeList.get(position).publishedAt,
                    "noDuration",
                    homeList.get(position).videoId,
                    "Songs"
            )
            playlistViewModel.addPlaylist(playListItem)
            Toast.makeText(requireContext(), "Add In Watch Later", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Add Into Watched Later", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "Important Added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addImportantDatabase(position: Int) {
        if (playlist.size > 0) {
            for (i in playlist){

                if (i.title.equals(homeList.get(position).title)){
                    val playListItem = PlayList(
                            0,
                            homeList.get(position).title,
                            homeList.get(position).thumbnails,
                            homeList.get(position).description,
                            homeList.get(position).publishedAt,
                            "noDuration",
                            homeList.get(position).videoId,
                            "Songs"
                    )
                    playlistViewModel.addPlaylist(playListItem)
                    Toast.makeText(requireContext(), "Add Into Watched Later", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "Important Added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playListDialog(position: Int){
        val alert = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.playlist_dialog_list, null)
        val important = view.findViewById<LinearLayout>(R.id.important_playlist_dialog)

        val alertDialog = alert.create()
        alertDialog.setView(view)
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.show()


        important.setOnClickListener{
            if (offline){ addSongsDatabase(position) } else { addSongs(position) }
            alertDialog.dismiss()
        }

    }

    private fun getDatbaseListCount() {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            homeList.addAll(it)
        })
    }

    private fun fetchFromDatabase () {
        itemList = ArrayList<Item>()
        itemList.clear()
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            homeList.addAll(it)
            progressbar.visibility = View.GONE
            if (homeList.size > 0) {
                offline = true
            }
            adapter2 = RelatedVideoDatabaseAdapter(requireContext(), homeList, this)
            recyclerView.adapter = adapter2
        })



    }

    private fun addHomeDatabase(position: Int) {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        //homeViewModel.addVideo(HomeDataset(0,itemList.get(position).etag, itemList.get(position).id,itemList.get(position).kind,itemList.get(position).snippet))



    }

    private fun cleanDataToBeShow () {
        val tempList = ArrayList<HomeDataset>()
        for (i in itemList) {
            for (j in homeList) {
                if (i.snippet.title.equals(j.title)){
                    tempList.add(j)
                    homeList.remove(j)
                    Log.d("monitorCleanData",i.snippet.title)
                }
            }
        }

        homeList.clear()
        homeList.addAll(tempList)
    }
}