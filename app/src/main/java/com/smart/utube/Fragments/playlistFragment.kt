package com.smart.utube.Fragments

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smart.utube.Activity.PlayerActivity
import com.smart.utube.DataBaseAdapter.PlayListAdapter
import com.smart.utube.R
import com.smart.utube.playlistData.PLayListViewModel
import com.smart.utube.playlistData.PlayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class playlistFragment : Fragment(), PlayListAdapter.setOnItemClick {
    lateinit var recyclerView: RecyclerView
    lateinit var pLayListViewModel: PLayListViewModel
    lateinit var playlist : ArrayList<PlayList>
    lateinit var adapter : PlayListAdapter

    var onlyone = true

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

        val view =  inflater.inflate(R.layout.fragment_playlist, container, false)

        //recyclerView Initialization
        recyclerView = view.findViewById(R.id.recycler_view_PlayList)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        //initialization
        playlist = ArrayList()
        adapter = PlayListAdapter(this)

        pLayListViewModel = ViewModelProvider(this).get(PLayListViewModel::class.java)
        pLayListViewModel.readAllData.observe(viewLifecycleOwner, Observer {

            if (onlyone) {
                playlist.addAll(it)
                adapter.addData(playlist)
                onlyone = false
            }
        })

        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        return view
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
                playlistFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun OnitemClick(position: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("position",position)
        intent.putExtra("VideoId",playlist.get(position).VideoId)
        intent.putExtra("title",playlist.get(position).title)
        intent.putExtra("description",playlist.get(position).description)
        intent.putExtra("publishDate",playlist.get(position).publish)
        intent.putExtra("thumbnail",playlist.get(position).thumbnail)
        startActivity(intent)
    }

    override fun onitemDelete(position: Int) {
        val delete = PlayList(
                playlist.get(position).id,
                playlist.get(position).title,
                playlist.get(position).thumbnail,
                playlist.get(position).description,
                playlist.get(position).publish,
                playlist.get(position).duration,
                playlist.get(position).VideoId,
                playlist.get(position).type
        )
        pLayListViewModel.deletePlayList(delete)
        playlist.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    override fun onItemShare(position: Int) {
        val shareApp = Intent(Intent.ACTION_SEND);
        shareApp.setType("text/plan");
        shareApp.putExtra(Intent.EXTRA_SUBJECT,"");
        shareApp.putExtra(Intent.EXTRA_TEXT,"https://youtu.be/"+playlist.get(position).VideoId);
        startActivity(Intent.createChooser(shareApp,"Share By"))
    }


}