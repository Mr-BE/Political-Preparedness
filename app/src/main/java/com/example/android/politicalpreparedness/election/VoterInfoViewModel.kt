package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.CivicRepository
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val dataSource: ElectionDao) : ViewModel() {

    //data entry point
    private val repo = CivicRepository(dataSource)

    //COMPLETED: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    //COMPLETED: Add var and methods to populate voter info
    private val _electionSaveStatus = MutableLiveData<Boolean>()
    val electionSaveStatus: LiveData<Boolean>
        get() = _electionSaveStatus

    private val _electionReceived = MutableLiveData<Election>()
    val electionReceived: LiveData<Election>
        get() = _electionReceived

    private val _infoLink = MutableLiveData<String>()
    val infoLink: LiveData<String>
        get() = _infoLink

    init {
//        setVoterInfo()

    }

    //get voter info from repo
    fun getVoterInfo(address: String, electionId: Int) {
        viewModelScope.launch {
            _voterInfo.value = repo.getVoterInfo(address, electionId)
        }
    }

    //Get election info to display by election id
    fun getElectionById(election: Election) {
        _electionReceived.value = election
    }

    //COMPLETED: Add var and methods to save and remove elections to local database
    fun updateElectionStatusSaved(election: Election) {
        viewModelScope.launch {
            election.Saved = true//change saved status
            _electionSaveStatus.value = election.Saved
            repo.updateElection(election)
        }
    }

    fun updateElectionStatusUnsaved(election: Election) {
        viewModelScope.launch {
            election.Saved = false//change saved status
            _electionSaveStatus.value = election.Saved
            repo.updateElection(election)
        }
    }


    //COMPLETED: Add var and methods to support loading URLs
    fun getSupportUrl(url: String) {
        _infoLink.value = url
    }

    //set voter info
//    fun setVoterInfo() {
//        _voterInfo.value = repo.voterInfo.value
//    }
//
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    //COMPLETE: Create Factory to generate VoterInfoViewModel with provided election datasource
    class VoterInfoViewModelFactory(private val electionDao: ElectionDao) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <viewModel : ViewModel?> create(modelClass: Class<viewModel>): viewModel {
            if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
                return VoterInfoViewModel(electionDao) as viewModel
            }
            throw IllegalArgumentException("viewModel error")
        }
    }
}
