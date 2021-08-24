package com.smart.utube.Activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.smart.utube.Adapter.SearchQueryAdapter
import com.smart.utube.Network.Repositry
import com.smart.utube.R
import com.smart.utube.SearchData.SearchDataModel
import com.smart.utube.SearchData.SearchViewModel
import com.smart.utube.SearchQuery.Item
import com.smart.utube.ViewModel.YoutubeViewModel
import com.smart.utube.ViewModel.YoutubeViewModelFactory
import com.smart.utube.databinding.ActivitySearchBinding
import com.smart.utube.playlistData.PLayListViewModel
import com.smart.utube.playlistData.PlayList


class SearchActivity : AppCompatActivity(), SearchQueryAdapter.setOnItemClick {
    private lateinit var binding : ActivitySearchBinding
    lateinit var toolbar : Toolbar
    lateinit var viewmodel : YoutubeViewModel
    lateinit var autoCompleteTextView: AutoCompleteTextView
    lateinit var searchViewModel : SearchViewModel
    lateinit var searchbtn : ImageView
    lateinit var itemList : ArrayList<Item>
    lateinit var adapter : ArrayAdapter<String>
    var searchList = ArrayList<SearchDataModel>()
    var list = ArrayList<String>()
    lateinit var searchAdapter : SearchQueryAdapter
    lateinit var speechRecognizer: SpeechRecognizer

    //playList
    lateinit var  playlistViewModel : PLayListViewModel
    lateinit var playlist : ArrayList<PlayList>

    lateinit var voice_btn: ImageView
    var PERMISSION = false


    var currentItem : Int = 0
    var totalItem : Int = 0
    var scrollOutItem : Int = 0
    lateinit var nextPageId : String
    lateinit var search : String
    lateinit var channelId : String
    lateinit var manager : LinearLayoutManager
    var isScrolling = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //custom Action bar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        voice_btn = findViewById(R.id.voice_recognaition)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        binding.recognzaitionView.setSpeechRecognizer(speechRecognizer)
        binding.recognzaitionView.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {

            }

            override fun onBeginningOfSpeech() {

            }

            override fun onRmsChanged(rmsdB: Float) {

            }

            override fun onBufferReceived(buffer: ByteArray?) {

            }

            override fun onEndOfSpeech() {
                binding.recognzaitionView.stop()
            }

            override fun onError(error: Int) {

            }

            override fun onResults(results: Bundle?) {
                if (results != null) {
                    showText(results)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {

            }

            override fun onEvent(eventType: Int, params: Bundle?) {

            }

        })

        val colors : IntArray = intArrayOf(
                Color.RED,
                Color.BLUE,
                Color.YELLOW,
                Color.GREEN,
                Color.WHITE
        )

        val height : IntArray = intArrayOf(50, 44, 68, 43, 56)

        binding.recognzaitionView.setColors(colors)
        binding.recognzaitionView.setBarMaxHeightsInDp(height)
        binding.recognzaitionView.setCircleRadiusInDp(4)
        binding.recognzaitionView.setSpacingInDp(6)
        binding.recognzaitionView.setIdleStateAmplitudeInDp(8)
        binding.recognzaitionView.setRotationRadiusInDp(16)
        binding.recognzaitionView.play()


        voice_btn.setOnClickListener{

            binding.recognzaitionView.visibility = View.VISIBLE

            Toast.makeText(this,"click",Toast.LENGTH_SHORT).show()

            Dexter.withContext(this)
                    .withPermission(Manifest.permission.RECORD_AUDIO)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                            PERMISSION = true
                            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            if(inputMethodManager.isAcceptingText()) { inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0) }
                            startRecogination()
                            binding.recognzaitionView.postDelayed(Runnable { startRecogination() }, 50)
                            binding.recognzaitionView.play()
                        }

                        override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                            PERMISSION = false
                        }

                        override fun onPermissionRationaleShouldBeShown(permissionRequest: PermissionRequest, permissionToken: PermissionToken) {
                            permissionToken.continuePermissionRequest()
                        }
                    }).check()


        }



        //room database
        playlistViewModel = ViewModelProvider(this).get(PLayListViewModel::class.java)
        getdata()

        //set up and intialized elements
        autoCompleteTextView = findViewById(R.id.autoGentrateTextView)

        //get Information about list
        searchViewModel = SearchViewModel(application)
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        searchViewModel.readAllSearch.observe(this, Observer { searches ->
            //search List
            searchList.addAll(searches)
            list = ArrayList<String>()
            for (i in searches) {
                list.add(i.name)
            }
            adapter = ArrayAdapter(this, R.layout.search_layout, R.id.title_search_layout, list)
            autoCompleteTextView.setAdapter(adapter)
        })

        //On search Click
        autoCompleteTextView.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {

                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> searchQueryValidation()
                }

                return false
            }

        })

        //adapter On Click
        binding.recyclerView.setHasFixedSize(true)
        manager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = manager

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                isScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                currentItem = manager.childCount
                scrollOutItem = manager.findFirstVisibleItemPosition()
                totalItem = manager.itemCount

                if (isScrolling && currentItem + scrollOutItem == totalItem) {
                    binding.progressbarSearch.visibility = View.VISIBLE
                    Handler().postDelayed(Runnable { LoadMoreData(nextPageId, search) }, 2000)
                }

                isScrolling = false
            }
        })


    }

    private fun LoadMoreData(nextToken: String, query: String) {
        val repositry = Repositry()
        val youtubeViewModelFactory= YoutubeViewModelFactory(repositry)

        viewmodel = YoutubeViewModel(repositry)
        viewmodel = ViewModelProvider(this, youtubeViewModelFactory)[YoutubeViewModel::class.java]
        viewmodel.getSearchNextPage(nextToken, query)
        viewmodel.searchList.observe(this, {
            nextPageId = it.nextPageToken

            Log.d("SearchList", "" + it.items.size)

            for (i in it.items) {
                itemList.add(Item(i.etag, i.id, i.kind, i.snippet))
                Log.d("SearchList", "" + itemList.size)
            }

            binding.progressbarSearch.visibility = View.GONE
            searchAdapter.notifyDataSetChanged()
//            searchAdapter = SearchQueryAdapter(this, itemList, this)
//            binding.recyclerView.adapter = searchAdapter

        })


    }


    fun getSearch(query: String){

        val repositry = Repositry()
        val youtubeViewModelFactory= YoutubeViewModelFactory(repositry)

        viewmodel = YoutubeViewModel(repositry)
        viewmodel = ViewModelProvider(this, youtubeViewModelFactory)[YoutubeViewModel::class.java]
        viewmodel.getSearchVideos(query)
        viewmodel.searchList.observe(this, {
            nextPageId = it.nextPageToken
            itemList = ArrayList()

            for (i in it.items) {
                itemList.add(Item(i.etag, i.id, i.kind, i.snippet))
                Log.d("Search", "" + itemList.get(0).snippet.title)
            }

            binding.progressbarSearch.visibility = View.GONE
            searchAdapter = SearchQueryAdapter(this, itemList, this)
            binding.recyclerView.adapter = searchAdapter
        })

    }

    override fun OnitemClick(position: Int) {
       val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("position", position)
        intent.putExtra("VideoId", itemList.get(position).id.videoId)
        intent.putExtra("title", itemList.get(position).snippet.title)
        intent.putExtra("description", itemList.get(position).snippet.description)
        intent.putExtra("publishDate", itemList.get(position).snippet.publishedAt)
        intent.putExtra("thumbnail", itemList.get(position).snippet.thumbnails.high.url)
        startActivity(intent)
    }

    override fun channelIDClick(position: Int) {
        Toast.makeText(this, "" + itemList.size + "   " + itemList.get(position).id.channelId, Toast.LENGTH_SHORT).show()
        //val intent = Intent(this, ChannelVideos::class.java)
        intent.putExtra("ChannelId", itemList.get(position).id.channelId)
        //startActivity(intent)
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

    private fun addSearch(searchKeyword: String) {
        val search = SearchDataModel(0, searchKeyword)
        searchViewModel.addSearch(search)
    }

    private fun searchQueryValidation() {
        search = autoCompleteTextView.text.toString()
        if (search.isEmpty()){
            Toast.makeText(this, "type Something", Toast.LENGTH_SHORT).show()
        }else{

            if (search.startsWith("https://")){

                val videoID = search.substring(17)
                urlData(videoID)

            }else {
                Toast.makeText(this, search, Toast.LENGTH_SHORT).show()
                binding.progressbarSearch.visibility = View.VISIBLE

                //search and create data

                if (!list.contains(search)) {
                    addSearch(search)
                    getSearch(search)
                } else {
                    getSearch(search)
                }

                //delete from database after more then 40

                if (list.size >= 40) {
                    searchViewModel.deleteSearch(searchList.get(0))
                }
            }
        }
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

            Toast.makeText(this, "Add In Playlist", Toast.LENGTH_SHORT).show()

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
                            "Songs"
                    )
                    playlistViewModel.addPlaylist(playListItem)
                    Toast.makeText(this, "song add", Toast.LENGTH_SHORT).show()
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
            addSongs(position)
            alertDialog.dismiss()
        }

    }

    private fun urlData(videoid: String) {

        val repositry = Repositry()
        val YoutubeViewModelFactory = YoutubeViewModelFactory(repositry)
        viewmodel = YoutubeViewModel(repositry)
        viewmodel = ViewModelProvider(this, YoutubeViewModelFactory)[YoutubeViewModel::class.java]
        viewmodel.getStaticVideoId(videoid)
        viewmodel.statisticsVideos.observe(this, {


            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("position", 0)
            intent.putExtra("VideoId", videoid)
            intent.putExtra("title", it.items[0].snippet.title)
            intent.putExtra("description", it.items[0].snippet.description)
            intent.putExtra("publishDate", it.items[0].snippet.publishedAt)
            intent.putExtra("thumbnail", it.items[0].snippet.thumbnails.high.url)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        })
    }

    private fun startRecogination () {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizer.startListening(intent)
    }

    private fun showText(result: Bundle){
        val match : ArrayList<String> = result.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
        binding.recognzaitionView.stop()
        binding.recognzaitionView.visibility = View.GONE
        binding.progressbarSearch.visibility = View.VISIBLE
        autoCompleteTextView.setText(match.get(0).toString())
        getSearch(match.get(0))
    }

}