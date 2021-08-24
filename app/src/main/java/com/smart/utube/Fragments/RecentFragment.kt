package com.smart.utube.Fragments

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smart.utube.Activity.PlayerActivity
import com.smart.utube.Data.Recent
import com.smart.utube.Data.RecentViewModel
import com.smart.utube.DataBaseAdapter.RecentAdapter
import com.smart.utube.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RecentFragment : Fragment(), RecentAdapter.OnRecentItemDelete {
    private lateinit var recyclerView: RecyclerView;
    private lateinit var recentViewModel: RecentViewModel
    private lateinit var adapter : RecentAdapter
    private var recentList = ArrayList<Recent>()

    var onlyone : Boolean = true

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
        val view =  inflater.inflate(R.layout.fragment_recent, container, false)

        recentViewModel = ViewModelProvider(this).get(RecentViewModel::class.java)
       // recentViewModel.deleteAll()

        recyclerView = view.findViewById(R.id.recyclerView_Recent)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = RecentAdapter(this)

        //load data
        recentViewModel.readAllData.observe(viewLifecycleOwner, Observer { recent ->

            Log.d("recentList", ""+recent.size)

            if (onlyone) {

                for (i in recent){
                    recentList.add(i)
                }

                adapter.addData(recentList)
                onlyone = false
            }
        })


        recyclerView.adapter = adapter

        Handler().postDelayed(Runnable { deleteFlow() },2000)
        return view

    }

    private fun deleteFlow() {
        if (recentList.size >= 40){
            val temp = recentList.size - 1
            val delete = Recent(
                    recentList.get(temp).id,
                    recentList.get(temp).title,
                    recentList.get(temp).thumbnail,
                    recentList.get(temp).description,
                    recentList.get(temp).publish,
                    recentList.get(temp).VideoId,
                    recentList.get(temp).time
            )
            recentViewModel.deleteRecent(delete)
           // Log.d("recentDelete", "RecentDeleted")
        }

        //Log.d("recentDelete", "Recent Not Deleted")
    }


    companion object {
        fun newInstance(param1: String, param2: String) =
                RecentFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun RecentDelete(position: Int) {
        recentViewModel.deleteRecent(recentList.get(position))
        adapter.notifyItemRemoved(position)
        recentList.removeAt(position)
    }

    override fun onItemPlayClick(position: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("position",position)
        intent.putExtra("VideoId",recentList.get(position).VideoId)
        intent.putExtra("title",recentList.get(position).title)
        intent.putExtra("description",recentList.get(position).description)
        intent.putExtra("publishDate",recentList.get(position).publish)
        intent.putExtra("thumbnail",recentList.get(position).thumbnail)
        intent.putExtra("time",recentList.get(position).time)
        intent.putExtra("fromRecent", true)
        startActivity(intent)
    }
}