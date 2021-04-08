package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.election.VoterInfoViewModel.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class VoterInfoFragment : Fragment() {

    private lateinit var infoViewModel: VoterInfoViewModel
    private lateinit var binding: FragmentVoterInfoBinding
    private lateinit var saveButton: Button

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //set up binding
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_voter_info,
                container, false)

        //view model data source
        val source = ElectionDatabase.getInstance(
                requireNotNull(this.activity).application).electionDao

        //COMPLETED: Add ViewModel values and create ViewModel
        infoViewModel = ViewModelProvider(this, VoterInfoViewModelFactory(source))
                .get(VoterInfoViewModel::class.java)

        //COMPLETED: Add binding values
        binding.lifecycleOwner = this
        binding.viewModel = infoViewModel

        //receive electron from fragment args
        val electionArgs = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElection

        //determine election by id
        infoViewModel.getElectionById(electionArgs)

        infoViewModel.electionReceived.observe(viewLifecycleOwner, {
            //get voter info
            infoViewModel.getVoterInfo()
        })
        infoViewModel.voterInfo.observe(viewLifecycleOwner, {
            infoViewModel.createSupportUrl()
        })


        //COMPLETED: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */


        //TODO: Handle loading of URLs

        saveButton = binding.followElectionButton

        //COMPLETED: Handle save button UI state
        infoViewModel.electionSaveStatus.observe(viewLifecycleOwner, { saveStatus ->
            if (!saveStatus) {
                saveButton.text = getText(R.string.follow_election)
            } else {
                saveButton.text = getText(R.string.unfollow_election)
            }
        })

        //COMPLETED: cont'd Handle save button clicks
        saveButton.setOnClickListener {
            if (infoViewModel.electionSaveStatus.value == true) {
                infoViewModel.updateElectionStatusUnsaved(electionArgs)
            } else {
                infoViewModel.updateElectionStatusSaved(electionArgs)
            }
        }

        //set url for info
//        infoViewModel.getSupportUrl(infoViewModel.voterInfo.value?.state
//                ?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl!!)

        //observe url value
        infoViewModel.infoLink.observe(viewLifecycleOwner, { urlString ->

            //check value of created url (dependent on API response)
            if (urlString.isNotBlank()) { //election has info url
                binding.electionInfoUrl.text = getText(R.string.click_here)
                binding.electionInfoUrl.setOnClickListener {
                    loadUrlOnClick(urlString)
                }
            } else { //no info url. Update UI
                binding.electionInfoUrl.text = getText(R.string.no_info)
            }

        })
        return binding.root
    }

    //COMPLETED: Create method to load URL intents
    private fun loadUrlOnClick(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}