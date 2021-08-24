package com.smart.utube.Activity


import android.Manifest
import android.app.ActionBar
import android.app.Activity
import android.app.usage.NetworkStatsManager
import android.app.usage.UsageStats
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.smart.utube.Adapter.RelatedVideoAdapter
import com.smart.utube.Data.Recent
import com.smart.utube.Data.RecentViewModel
import com.smart.utube.Network.Repositry
import com.smart.utube.RelatedVideoDataset.Item
import com.smart.utube.ViewModel.YoutubeViewModel
import com.smart.utube.ViewModel.YoutubeViewModelFactory
import com.smart.utube.databinding.ActivityPlayerBinding
import com.smart.utube.playlistData.PLayListViewModel
import com.smart.utube.playlistData.PlayList


class PlayerActivity : AppCompatActivity(),RelatedVideoAdapter.setOnItemClick {
    private lateinit var binding : ActivityPlayerBinding
    lateinit var decorView : View
    lateinit var recentViewModel : RecentViewModel
    lateinit var recentList : ArrayList<Recent>
    lateinit var viewmodel : YoutubeViewModel

    lateinit var youtubeViewModel : YoutubeViewModel
    lateinit var progressbar : ProgressBar
    var itemList : ArrayList<Item> = ArrayList()
    lateinit var RelatedAdapter : RelatedVideoAdapter
    var onlyone : Boolean = true

    //playList
    lateinit var  playlistViewModel : PLayListViewModel
    lateinit var playlist : ArrayList<PlayList>


    lateinit var youtubeplayer : YouTubePlayer
    var backIsAvailable : Boolean = true
    var position : Int = 0
    var category : String = "Empty"
    var title : String = "Empty"
    var description : String = "Empty"
    var publish : String = "Empty"
    var thumbnail : String = "Empty"
    var time : Float = 0f
    var onlyOneTime = true
    var fromRecent : Boolean = false
    var permissionForWrite : Boolean = false
    lateinit var networkStatsManager : NetworkStatsManager
    lateinit var telephonyManager : TelephonyManager

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        position = intent.getIntExtra("position", 0)
        category = intent.getStringExtra("VideoId")!!
        title = intent.getStringExtra("title")!!
        description = intent.getStringExtra("description")!!
        publish = intent.getStringExtra("publishDate")!!
        thumbnail = intent.getStringExtra("thumbnail")!!
        time = intent.getFloatExtra("time", 0.0f)
        fromRecent = intent.getBooleanExtra("fromRecent", false)

        recentViewModel = RecentViewModel(application)
        recentViewModel = ViewModelProvider(this).get(RecentViewModel::class.java)


        getExtraData()

        binding.descriptionHeaderPlayer.setOnClickListener{
            if (binding.descriptionPlayer.visibility == View.VISIBLE){
                binding.descriptionPlayer.visibility = View.GONE
                binding.hintdescription.visibility = View.VISIBLE
            } else {
                binding.descriptionPlayer.visibility = View.VISIBLE
                binding.hintdescription.visibility = View.GONE
            }
        }

        binding.arrowClick.setOnClickListener{
            if(binding.arrowClick.visibility == View.VISIBLE){
                binding.iconLayout.visibility = View.GONE
                binding.titleBlock.visibility = View.VISIBLE
                binding.fullScreenBarier.visibility = View.VISIBLE
                youtubeplayer.pause()
            }
        }

        binding.sliderLayout.setOnClickListener{
            if (binding.iconLayout.visibility == View.GONE){
//                binding.sliderLayout.visibility = View.VISIBLE
                 binding.titleBlock.visibility = View.GONE
                 binding.iconLayout.visibility = View.VISIBLE
                 binding.fullScreenBarier.visibility = View.GONE
                youtubeplayer.play()
            }
        }

        binding.floatingWindow.setOnClickListener{
            val intent = Intent(this, floatingWindows::class.java)
            intent.putExtra("VideoId", category)
            intent.putExtra("SkipTime", time)
            startActivity(intent)
        }

        binding.sharePlayer.setOnClickListener{
            val shareApp = Intent(Intent.ACTION_SEND);
            shareApp.setType("text/plan");
            shareApp.putExtra(Intent.EXTRA_SUBJECT, "");
            shareApp.putExtra(Intent.EXTRA_TEXT, "https://youtu.be/" + category);
            startActivity(Intent.createChooser(shareApp, "Share By"))
        }

        binding.suggestionTriggered.setOnClickListener{

            getList(category)
            if (onlyOneTime) {
                onlyOneTime = false
                Log.d("youtubePlayer", "Suggested request to server")
            }
                binding.suggestionBlock.visibility = View.VISIBLE
                binding.suggestionProgressBar.visibility = View.VISIBLE

        }

        binding.closeSuggestion.setOnClickListener{ binding.suggestionBlock.visibility = View.GONE }

        binding.downloadVideo.setOnClickListener{ DownloadDialog() }

        binding.statiistics.setOnClickListener{ AlertDialogBox() }

        //getTrendingData()
        loadVideo(category, time)

        configurationCheck()

        decorView = window.decorView
        decorView.setOnSystemUiVisibilityChangeListener(View.OnSystemUiVisibilityChangeListener { visibility ->
            if (visibility == 0) {
                decorView.setSystemUiVisibility(hideSystemBar())
            }
        })
    }

    //for full screen


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBar())
        }
    }

    private fun hideSystemBar(): Int {
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    )
    }

    private fun loadVideo(videoId: String, playTime: Float) {
        binding.youtubePlayer.addYouTubePlayerListener(object : YouTubePlayerListener {
            override fun onApiChange(youTubePlayer: YouTubePlayer) {

            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                time = second

            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {

            }

            override fun onPlaybackQualityChange(
                    youTubePlayer: YouTubePlayer, playbackQuality: PlayerConstants.PlaybackQuality
            ) {

            }

            override fun onPlaybackRateChange(
                    youTubePlayer: YouTubePlayer,
                    playbackRate: PlayerConstants.PlaybackRate
            ) {

            }

            override fun onReady(youTubePlayer: YouTubePlayer) {
                youtubeplayer = youTubePlayer
                youTubePlayer.loadVideo(videoId, playTime)


            }

            override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
            ) {
                Log.d("youtubePlayer", "onStateChange")
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {

            }

            override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {

            }

            override fun onVideoLoadedFraction(
                    youTubePlayer: YouTubePlayer,
                    loadedFraction: Float
            ) {
                if (onlyone) {
                    //binding.iconLayout.visibility = View.VISIBLE
                    onlyone = false
                }
                binding.progrssbarPlayer.visibility = View.GONE
            }

        })
    }

    override fun onBackPressed() {
        if (backIsAvailable) {
            if (!fromRecent) { addRecentData() } else { updateRecent() }
            youtubeplayer.pause()
            binding.youtubePlayer.release()
            //finish()
            super.onBackPressed()
        }else{
            Toast.makeText(this, "Please Wait for Exit Until Video Is Not Loaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addRecentData() {
        var found = false
        if (recentList.size > 0) {
            for (i in recentList) {
                if (i.title.equals(title)) {
                    found = true
                    Toast.makeText(this, "already preesent ", Toast.LENGTH_SHORT).show()
                }
            }

            if (!found){
                val recent = Recent(0, title, thumbnail, description, publish, category, time)
                recentViewModel.addRecent(recent)
            }

        }else{
            val recent = Recent(0, title, thumbnail, description, publish, category, time)
            recentViewModel.addRecent(recent)
            Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateRecent() {
        if (recentList.size > 0) {
            for (i in recentList){
                if (i.title.equals(title)){
                    val recent = Recent(i.id, title, thumbnail, description, publish, category, time)
                    recentViewModel.updateRecent(recent)
                    Toast.makeText(this, "updated  " + time, Toast.LENGTH_SHORT).show()
                }
            }

        }else{
            //nothing
        }
    }

    private fun preventForCopyRecentList () {
        recentList = ArrayList<Recent>()
        recentViewModel.readAllData.observe(this, Observer {
            recentList.addAll(it)
        })
    }

    private fun AlertDialogBox (){
        val alert = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(com.smart.utube.R.layout.statistics, null)

        val likes : TextView = view.findViewById(com.smart.utube.R.id.likes_Count)
        val dislikes : TextView = view.findViewById(com.smart.utube.R.id.dislikes_Count)
        val views : TextView = view.findViewById(com.smart.utube.R.id.views_Count)

        val alertDialog : AlertDialog = alert.create()
        alertDialog.setView(view)
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.show()


        //send request and getData

        val repositry = Repositry()
        val YoutubeViewModelFactory = YoutubeViewModelFactory(repositry)
        viewmodel = YoutubeViewModel(repositry)
        viewmodel = ViewModelProvider(this, YoutubeViewModelFactory)[YoutubeViewModel::class.java]
        viewmodel.getStaticVideoId(category)
        viewmodel.statisticsVideos.observe(this, {


            likes.setText(" (" + it.items.get(0).statistics.likeCount + ")")
            dislikes.setText( " (" + it.items.get(0).statistics.dislikeCount + ")")
            views.setText(" (" + it.items.get(0).statistics.viewCount + ")")

        })



    }

    fun getList(videoid: String) {
        val repositry = Repositry()
        val YoutubeViewModelFactory = YoutubeViewModelFactory(repositry)
        viewmodel = YoutubeViewModel(repositry)
        viewmodel = ViewModelProvider(this,YoutubeViewModelFactory) [YoutubeViewModel::class.java]
        viewmodel.getRelatedVideos(videoid)
        Log.d("ideses",videoid)
        viewmodel.listofRelatedVideos.observe(this,{

            itemList = ArrayList<Item>()

            for (i in it.items){
                itemList.add(Item(i.etag,i.id,i.kind,i.snippet))
                Log.d("ideses",i.snippet.title)
            }
            binding.suggestionProgressBar.visibility = View.GONE
            val adapter = RelatedVideoAdapter(this,itemList,this)
            binding.recyclerViewSuggestion.adapter = adapter
        })

    }

    override fun OnitemClick(position: Int) {
        youtubeplayer.loadVideo(itemList.get(position).id.videoId, 0f)
        binding.titlePlayer.setText(itemList.get(position).snippet.title)
        binding.descriptionPlayer.setText(itemList.get(position).snippet.description)
        binding.videoId.setText(itemList.get(position).id.videoId)
        binding.suggestionBlock.visibility = View.GONE
        binding.iconLayout.visibility = View.GONE
        binding.titleBlock.visibility = View.GONE
        category = itemList.get(position).id.videoId
        binding.progrssbarPlayer.visibility = View.VISIBLE
        onlyOneTime = true
        val recent = Recent(
                0,
                itemList.get(position).snippet.title,
                itemList.get(position).snippet.thumbnails.high.url,
                itemList.get(position).snippet.description,
                itemList.get(position).snippet.publishedAt,
                itemList.get(position).id.videoId,
                0.0f
        )
        recentViewModel.addRecent(recent)
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


//    fun getdata(){
//        playlist = ArrayList()
//        playlistViewModel.readAllData.observe(this, Observer {
//            playlist.addAll(it)
//        })
//    }

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
                Toast.makeText(this, "Add In Playlist", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Add In Playlist", Toast.LENGTH_SHORT).show()
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
                            "Important"
                    )
                    playlistViewModel.addPlaylist(playListItem)
                    Toast.makeText(this, "Add Important", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Add Important", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playListDialog(position: Int){
        val alert = android.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(com.smart.utube.R.layout.playlist_dialog_list, null)
        val important = view.findViewById<LinearLayout>(com.smart.utube.R.id.important_playlist_dialog)

        val alertDialog = alert.create()
        alertDialog.setView(view)
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.show()


        important.setOnClickListener{
            addSongs(position)
            alertDialog.dismiss()
        }

    }

    private fun DownloadDialog(){
        val alert = android.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(com.smart.utube.R.layout.download_layout, null)

        val music_btn = view.findViewById<LinearLayout>(com.smart.utube.R.id.music_click)
        val video_btn = view.findViewById<LinearLayout>(com.smart.utube.R.id.video_click)


        val alertDialog = alert.create()
        alertDialog.setView(view)
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.show()

        val obj = DownloadAudioAndVideo()

        music_btn.setOnClickListener {
//            obj.AudioDownloader(this,category)
//            Handler().postDelayed(Runnable { Toast.makeText(this,obj.response,Toast.LENGTH_SHORT).show() },4000)

            val intent = Intent(this, DownloadActivity::class.java)
            intent.putExtra("operation","song");
            intent.putExtra("videoID",category)
            startActivity(intent)
            alertDialog.dismiss() }

        video_btn.setOnClickListener {
//            obj.VideoDownloader(this, category)
//            Handler().postDelayed(Runnable { Toast.makeText(this,obj.response,Toast.LENGTH_SHORT).show() },4000)

            val intent = Intent(this, DownloadActivity::class.java)
            intent.putExtra("operation","video");
            intent.putExtra("videoID",category)
            startActivity(intent)
            alertDialog.dismiss()}

    }

    fun getExtraData() {
        binding.recyclerViewSuggestion.setHasFixedSize(true)
        binding.recyclerViewSuggestion.layoutManager = LinearLayoutManager(this)

        playlistViewModel = ViewModelProvider(this).get(PLayListViewModel::class.java)
        //getdata()

        preventForCopyRecentList()

        binding.titlePlayer.setText(title)
        binding.descriptionPlayer.setText(description)
        binding.hintdescription.setText(description)
        binding.videoId.setText(category)



        publish = publish.substring(0, 10)
        binding.publishPlayer.setText("Publish At : " + publish)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            binding.fullScreenBarier.visibility = View.GONE
            binding.titleBlock.visibility = View.GONE
            Toast.makeText(this, "landscape",Toast.LENGTH_SHORT).show()

        }else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            binding.fullScreenBarier.visibility = View.VISIBLE
            binding.titleBlock.visibility = View.VISIBLE
            Toast.makeText(this, "portrate",Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurationCheck() {

        val orientaion = application.resources.configuration.orientation

        if(orientaion == 1) {
            binding.fullScreenBarier.visibility = View.VISIBLE
            binding.titleBlock.visibility = View.VISIBLE
        }else if (orientaion == 2) {
            binding.fullScreenBarier.visibility = View.GONE
            binding.titleBlock.visibility = View.GONE
        }
    }

}

