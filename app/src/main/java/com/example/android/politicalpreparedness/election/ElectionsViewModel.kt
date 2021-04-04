package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.CivicRepository
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(electionDao: ElectionDao) : ViewModel() {

    private val repo = CivicRepository(electionDao)

    //COMPLETED: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElection: LiveData<List<Election>>
        get() = _upcomingElections

    //COMPLETED: Create live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    fun populateUpcomingElections() {
    }

    val savedElectionItems = repo.savedElectionList
    val upcomingElectionItems = repo.electionList

    init {
        viewModelScope.launch {
            repo.refreshElection()
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info


    //Completed: Create Factory to generate ElectionViewModel with provided election datasource
    class ElectionsViewModelFactory(private val electionDao: ElectionDao) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <viewModel : ViewModel?> create(modelClass: Class<viewModel>): viewModel {
            if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
                return ElectionsViewModel(electionDao) as viewModel
            }
            throw IllegalArgumentException("viewModel error")
        }
    }

}