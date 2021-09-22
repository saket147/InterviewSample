package com.example.bigstepsampleapp.ui.main.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bigstepsampleapp.R
import com.example.bigstepsampleapp.database.viewmodel.VideosTableViewModel
import com.example.bigstepsampleapp.network.api.ApiHelper
import com.example.bigstepsampleapp.network.api.RetrofitBuilder
import com.example.bigstepsampleapp.network.model.Results
import com.example.bigstepsampleapp.network.model.VideosApiResponse
import com.example.bigstepsampleapp.ui.base.ViewModelFactory
import com.example.bigstepsampleapp.ui.main.adapter.MainAdapter
import com.example.bigstepsampleapp.ui.main.interfaces.VideoItemClickInterface
import com.example.bigstepsampleapp.ui.main.viewmodel.MainViewModel
import com.example.bigstepsampleapp.utils.ConnectionUtil
import com.example.bigstepsampleapp.utils.Status
import kotlinx.android.synthetic.main.fragment_videos.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideosFragment : Fragment(), VideoItemClickInterface {
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MainAdapter
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var videosTableViewModel: VideosTableViewModel? = null
    private var videosApiResponse: VideosApiResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI()
        val connectionUtil: ConnectionUtil? = activity?.let { ConnectionUtil(it) }

        if (connectionUtil?.isOnline() == true) {
            setupObservers()
        } else {
            progressBar?.visibility = View.GONE
            recyclerView?.visibility = View.GONE
            tvError?.visibility = View.VISIBLE
            tvError?.text = "No Internet connection!"
        }
        activity?.let {
            ConnectionUtil(it).onInternetStateListener(object :
                ConnectionUtil.ConnectionStateListener {
                override fun onAvailable(isAvailable: Boolean) {
                    uiScope.launch {
                        if (isAvailable) {
                            if (videosApiResponse == null) {
                                setupObservers()
                            }
                        } else {
                            if (videosApiResponse == null) {
                                progressBar?.visibility = View.GONE
                                recyclerView?.visibility = View.GONE
                                tvError?.visibility = View.VISIBLE
                                tvError?.text = "No Internet connection!"
                            }
                        }
                    }
                }
            })
        }
    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(this, ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))).get(
                MainViewModel::class.java
            )
        videosTableViewModel =
            activity?.application?.let {
                ViewModelProvider.AndroidViewModelFactory.getInstance(it)
                    .create(VideosTableViewModel::class.java)
            }
    }

    //initialising the adapter
    private fun setupUI() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = MainAdapter(arrayListOf(), this)
        recyclerView?.addItemDecoration(
            DividerItemDecoration(
                recyclerView?.context,
                (recyclerView?.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView?.adapter = adapter
    }

    //observing the response from the api
    private fun setupObservers() {
        viewModel.getVideos(term = "Michael+jackson", media = "musicVideo")
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            recyclerView?.visibility = View.VISIBLE
                            progressBar?.visibility = View.GONE
                            tvError?.visibility = View.GONE
                            resource.data?.let { videosResponse: VideosApiResponse ->
                                this.videosApiResponse = videosResponse
                                retrieveList(
                                    videosResponse.results
                                )
                            }
                        }
                        Status.ERROR -> {
                            recyclerView?.visibility = View.VISIBLE
                            progressBar?.visibility = View.GONE
                            tvError?.visibility = View.VISIBLE
                            tvError?.text = it.message
                            Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            progressBar?.visibility = View.VISIBLE
                            recyclerView?.visibility = View.GONE
                            tvError?.visibility = View.GONE
                        }
                    }
                }
            })
    }

    //notifying adapter changes
    //sending data to adapter class
    private fun retrieveList(results: Collection<Results?>?) {
        adapter.apply {
            addUsers(results)
            notifyDataSetChanged()
        }
    }

    //click listener for recyclerview item
    override fun onItemClick(result: Results?) {
        uiScope.launch {
            videosTableViewModel?.insertItem(result)
            Toast.makeText(
                activity,
                result?.trackName + " is now saved in database",
                Toast.LENGTH_LONG
            ).show()
        }
        Log.d("onItemClick", "Clicked on Saved history item")
    }
}