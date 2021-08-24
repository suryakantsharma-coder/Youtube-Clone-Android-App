package com.smart.utube.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smart.utube.Activity.PlayerActivity
import com.smart.utube.Adapter.VideoAdapter
import com.smart.utube.DataClasses.Item
import com.smart.utube.Network.Repositry
import com.smart.utube.R
import com.smart.utube.ViewModel.YoutubeViewModel
import com.smart.utube.ViewModel.YoutubeViewModelFactory
import com.smart.utube.playlistData.PLayListViewModel
import com.smart.utube.playlistData.PlayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExploreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExploreFragment : Fragment(), VideoAdapter.setOnItemClick {
    lateinit var viewmodel : YoutubeViewModel
    lateinit var recyclerView : RecyclerView
    lateinit var warning : TextView
    lateinit var progressbar : LinearLayout
    lateinit var itemList : ArrayList<Item>
    lateinit var  playlistViewModel : PLayListViewModel
    lateinit var playlist : ArrayList<PlayList>
    lateinit var adapter : VideoAdapter

    var currentItem : Int = 0
    var totalItem : Int = 0
    var scrollOutItem : Int = 0
    lateinit var nextPageToken : String
    lateinit var search : String
    lateinit var manager : LinearLayoutManager
    var isScrolling = false

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_explore, container, false)


        progressbar = view.findViewById(R.id.progress_Explore)
        recyclerView  = view.findViewById(R.id.recycler_view_Explore)
        warning = view.findViewById(R.id.warning_message)

        recyclerView.setHasFixedSize(true)
        manager = LinearLayoutManager(context)
        recyclerView.layoutManager = manager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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
                    progressbar.visibility = View.VISIBLE
                    Handler().postDelayed(Runnable { getNetxPageList(nextPageToken) },2000)
                }

                isScrolling = false

            }
        })

        playlistViewModel = ViewModelProvider(this).get(PLayListViewModel::class.java)

        val sharedPreferences = requireActivity().getSharedPreferences("exploreSection", AppCompatActivity.MODE_PRIVATE)

        val enable = sharedPreferences.getBoolean("exploreEnable",false)

        if (enable) {
            getdata()
            getList()
            warning.visibility = View.GONE
        } else{
            progressbar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            warning.visibility = View.VISIBLE
        }






        return view
    }

    private fun getNetxPageList( nextPageToken : String) {

        val repositry = Repositry()
        val YoutubeViewModelFactory = YoutubeViewModelFactory(repositry)
        viewmodel = YoutubeViewModel(repositry)
        viewmodel = ViewModelProvider(this,YoutubeViewModelFactory) [YoutubeViewModel::class.java]
        viewmodel.getNextPageTrendingVideos(nextPageToken)
        viewmodel.trendingVideosList.observe(viewLifecycleOwner,{

            this.nextPageToken = it.nextPageToken

            itemList = ArrayList<Item>()

            for (i in it.items){
                itemList.add(Item(i.contentDetails,i.etag,i.id,i.kind,i.snippet,i.statistics))
                Log.d("listing", ""+itemList.get(0).statistics.viewCount);
            }
            progressbar.visibility = View.GONE
            adapter.notifyDataSetChanged()
        })

    }

    private fun getList() {

        val repositry = Repositry()
        val YoutubeViewModelFactory = YoutubeViewModelFactory(repositry)
        viewmodel = YoutubeViewModel(repositry)
        viewmodel = ViewModelProvider(this,YoutubeViewModelFactory) [YoutubeViewModel::class.java]
        viewmodel.getTrendingVideos()
        viewmodel.trendingVideosList.observe(viewLifecycleOwner,{

            this.nextPageToken = it.nextPageToken

            itemList = ArrayList<Item>()

            for (i in it.items){
                itemList.add(Item(i.contentDetails,i.etag,i.id,i.kind,i.snippet,i.statistics))
                Log.d("listing", ""+itemList.get(0).statistics.viewCount);
            }
            progressbar.visibility = View.GONE
            adapter = VideoAdapter(requireContext(),itemList,this)
            recyclerView.adapter = adapter
        })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExploreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ExploreFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun OnitemClick(position: Int) {
        Log.d("listing", ""+itemList.get(position).statistics.viewCount)
        val intent = Intent(context,PlayerActivity::class.java)
        intent.putExtra("position",position)
        intent.putExtra("VideoId",itemList.get(position).id)
        intent.putExtra("title",itemList.get(position).snippet.title)
        intent.putExtra("description",itemList.get(position).snippet.description)
        intent.putExtra("publishDate",itemList.get(position).snippet.publishedAt)
        intent.putExtra("thumbnail",itemList.get(position).snippet.thumbnails.high.url)
        startActivity(intent)
    }

    override fun addPlayList(position: Int) {
       playListDialog(position)
    }

    override fun onItemShare(position: Int) {
        val shareApp = Intent(Intent.ACTION_SEND);
        shareApp.setType("text/plan");
        shareApp.putExtra(Intent.EXTRA_SUBJECT,"");
        shareApp.putExtra(Intent.EXTRA_TEXT,"https://youtu.be/"+itemList.get(position).id);
        startActivity(Intent.createChooser(shareApp,"Share By"))
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
                        itemList.get(position).contentDetails.duration,
                        itemList.get(position).id,
                        "Songs"
                )
                playlistViewModel.addPlaylist(playListItem)
                Toast.makeText(requireContext(),"Add In Playlist",Toast.LENGTH_SHORT).show()
            }

        }else{
            val playListItem = PlayList(
                    0,
                    itemList.get(position).snippet.title,
                    itemList.get(position).snippet.thumbnails.high.url,
                    itemList.get(position).snippet.description,
                    itemList.get(position).snippet.publishedAt,
                    itemList.get(position).contentDetails.duration,
                    itemList.get(position).id,
                    "Songs"
            )
            playlistViewModel.addPlaylist(playListItem)
            Toast.makeText(requireContext(),"Add In Playlist",Toast.LENGTH_SHORT).show()
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
                            itemList.get(position).contentDetails.duration,
                            itemList.get(position).id,
                            "Songs"
                    )
                    playlistViewModel.addPlaylist(playListItem)
                    Toast.makeText(requireContext(),"Important Added",Toast.LENGTH_SHORT).show()
                }

            }

        }else{
            val playListItem = PlayList(
                    0,
                    itemList.get(position).snippet.title,
                    itemList.get(position).snippet.thumbnails.high.url,
                    itemList.get(position).snippet.description,
                    itemList.get(position).snippet.publishedAt,
                    itemList.get(position).contentDetails.duration,
                    itemList.get(position).id,
                    "Important"
            )
            playlistViewModel.addPlaylist(playListItem)
            Toast.makeText(requireContext(),"Important Added",Toast.LENGTH_SHORT).show()
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
            addSongs(position)
            alertDialog.dismiss()
        }

    }
}