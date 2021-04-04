package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModel.*
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment : Fragment() {
    private lateinit var binding: FragmentElectionBinding

    //COMPLETED: Declare ViewModel
    private lateinit var electionViewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding =
                DataBindingUtil.inflate(
                        inflater, R.layout.fragment_election, container, false
                )

        binding.lifecycleOwner = this

        //important vals
        val app = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(app).electionDao

        //COMPLETED: Add ViewModel values and create ViewModel
        //init viewModel
        electionViewModel = ViewModelProvider(this, ElectionsViewModelFactory(dataSource))
                .get(ElectionsViewModel::class.java)

        //COMPLETED: Add binding values
        binding.viewModel = electionViewModel

        //adapter setup
        val upcomingAdapter = ElectionListAdapter(ElectionListener { election ->
            findNavController()
                    .navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election))
        })
        //COMPLETED: Initiate recycler adapters

        //COMPLETED: Populate recycler adapters
        electionViewModel.upcomingElectionItems.observe(viewLifecycleOwner, { electionList ->
            electionList.let {
//                upcomingAdapter.submitList(electionList)
            }

        })
        binding.upcomingElectionsList.adapter = upcomingAdapter

        //COMPLETED: Link elections to voter info
        val saveAdapter = ElectionListAdapter(ElectionListener { election ->
            findNavController()
                    .navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election))
        })

        electionViewModel.savedElectionItems.observe(viewLifecycleOwner, { savedElectionList ->
            savedElectionList.let {
                saveAdapter.submitList(savedElectionList)
            }
        })
        binding.savedElectionsList.adapter = saveAdapter


        return binding.root


    }

    //TODO: Refresh adapters when fragment loads

}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Election>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter
    adapter.submitList(data)
}