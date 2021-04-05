package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.election.VoterInfoViewModel.*

class VoterInfoFragment : Fragment() {

    private lateinit var infoViewModel: VoterInfoViewModel
    private lateinit var binding: FragmentVoterInfoBinding
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //set up binding
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_voter_info,
                container, false)

        //view model data source
        val dataSource = ElectionDatabase.getInstance(
                requireNotNull(this.activity).application).electionDao

        //COMPLETED: Add ViewModel values and create ViewModel
        infoViewModel = ViewModelProvider(this, VoterInfoViewModelFactory(dataSource))
                .get(VoterInfoViewModel::class.java)

        //COMPLETED: Add binding values
        binding.lifecycleOwner = this
        binding.viewModel = infoViewModel

        //receive electron from fragment args
        val electionArgs = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElection

        //determine election by id
        infoViewModel.getElectionById(electionArgs)

        //get voter info
        infoViewModel.getVoterInfo(electionArgs.division.id, electionArgs.id)


        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        return binding.root

    }

    //TODO: Create method to load URL intents

}