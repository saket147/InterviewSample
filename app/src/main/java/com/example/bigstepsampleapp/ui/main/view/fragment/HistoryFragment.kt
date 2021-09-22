package com.example.bigstepsampleapp.ui.main.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bigstepsampleapp.R
import com.example.bigstepsampleapp.database.viewmodel.VideosTableViewModel
import com.example.bigstepsampleapp.network.model.Results
import com.example.bigstepsampleapp.ui.main.adapter.MainAdapter
import com.example.bigstepsampleapp.ui.main.interfaces.VideoItemClickInterface
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryFragment : Fragment(), VideoItemClickInterface {
    private var viewModel: VideosTableViewModel? = null
    private lateinit var adapter: MainAdapter
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI()
        setupObservers()
    }

    private fun setupViewModel() {
        viewModel =
            activity?.application?.let {
                ViewModelProvider.AndroidViewModelFactory.getInstance(it)
                    .create(VideosTableViewModel::class.java)
            }
    }

    //initialising the adapter
    private fun setupUI() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = MainAdapter(arrayListOf(), this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        recyclerView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        uiScope.launch {
            viewModel?.savedVideos?.collect { results ->
                retrieveList(results)
            }
        }
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
        Log.d("onItemClick", "Clicked on Saved history item")
    }
}