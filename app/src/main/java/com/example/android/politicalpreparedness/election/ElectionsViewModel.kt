package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.CivicRepository
import kotlinx.coroutines.launch

//COMPLETED: Construct ViewModel and provide election datasource
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

    //determine whether to show loading progress
    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    //monitor navigation
    private val _navigationValue = MutableLiveData<Boolean>()
    val navigationValue: LiveData<Boolean>
        get() = _navigationValue

    //COMPLETED: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    val savedElectionItems = repo.savedElectionList
    val upcomingElectionItems = repo.electionList

    init {
        viewModelScope.launch {
            repo.refreshElection()
        }
    }

    fun checkLoading() {
        _showLoading.value = repo.isLoading.value != true
    }

    //COMPLETED: Create functions to navigate to saved or upcoming election voter info

    fun navigateOut() {
        _navigationValue.value = true
    }

    fun doneNavigation() {
        _navigationValue.value = true
    }

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