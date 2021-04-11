package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModel.ElectionsViewModelFactory
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment : Fragment() {
    private lateinit var binding: FragmentElectionBinding

    //COMPLETED: Declare ViewModel
    private lateinit var electionViewModel: ElectionsViewModel

    private lateinit var upcomingAdapter: ElectionListAdapter
    private lateinit var saveAdapter: ElectionListAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
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
        electionViewModel.checkLoading()

//        electionViewModel.showLoading.observe(viewLifecycleOwner, { value ->
//            if (value == true) {
//                binding.progressBar.visibility = View.VISIBLE
//                binding.progressBar.isAnimating
//                electionViewModel.checkLoading()
//            }else{
//                binding.progressBar.visibility = View.GONE
//            }
//
//        })

        //adapter setup
        upcomingAdapter = ElectionListAdapter(ElectionListener { election ->
            findNavController()
                    .navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election))
        })
        //COMPLETED: Initiate recycler adapters

        //COMPLETED in xml: Populate recycler adapters

        binding.upcomingElectionsList.adapter = upcomingAdapter

        //COMPLETED: Link elections to voter info
        saveAdapter = ElectionListAdapter(ElectionListener { election ->
            electionViewModel.navigateOut()
            findNavController()
                    .navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election))
            electionViewModel.doneNavigation()
        })

//        electionViewModel.savedElectionItems.observe(viewLifecycleOwner, { savedElectionList ->
//            savedElectionList.let {
//                saveAdapter.submitList(savedElectionList)
//            }
//        })
        binding.savedElectionsList.adapter = saveAdapter


        return binding.root


    }

    //CO: Refresh adapters when fragment loads
    override fun onResume() {
        super.onResume()
        upcomingAdapter.notifyDataSetChanged()
        saveAdapter.notifyDataSetChanged()

    }
}
