package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.ElectionDao
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.repository.CivicRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val dataSource: ElectionDao) : ViewModel() {

    val repo = CivicRepository(dataSource)

    //COMPLETED: Establish live data for representatives and address
    private val _foundRepsResponse = MutableLiveData<RepresentativeResponse>()
    val foundRepsResponse: LiveData<RepresentativeResponse>
        get() = _foundRepsResponse

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    //Address value
    private val _userAddress = MutableLiveData<String>()
    val userAddress: LiveData<String>
        get() = _userAddress

    //progressBar
    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean>
        get() = _showProgress

    fun checkProgressStatus() {
        _showProgress.value = repo.isLoading.value != true
        doneProgress()
    }

    private fun doneProgress() {
        _showProgress.value = repo.isLoading.value != true
        _showProgress.value = false
    }


    //COMPLETE: Create function to get address from individual fields
    //Address fields
    var address1: String = ""
    var address2: String? = null
    var city: String = ""
    var zipCode: String = ""
    var state = MutableLiveData<String>()

    fun setUpAddress() {
//        _userAddress.postValue(state.value?.let { Address(address1, address2, city, it, zipCode).toFormattedString() })
        _userAddress.value = state.value?.let { Address(address1, address2, city, it, zipCode).toFormattedString() }
    }


    //COMPLETED: Create function to fetch representatives from API from a provided address
    fun fetchReps(address: String) {

        viewModelScope.launch {
            _showProgress.postValue(true)
            _foundRepsResponse.value = _userAddress.value?.let { repo.getReps(it) }
        }
        val offices = _foundRepsResponse.value?.offices
        val officials = _foundRepsResponse.value?.officials
        _representatives.value = offices?.flatMap { office -> office.getRepresentatives(officials!!) }
        if (_representatives.value != null) _showProgress.postValue(false)
    }


    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //COMPLETED: Create function get address from geo location
    fun getAddressFromGeo(address: Address) {
        _userAddress.value = address.toFormattedString()
        fetchReps(_userAddress.value.toString())
    }

    class RepresentativeViewModelFactory(private val electionDao: ElectionDao) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <viewModel : ViewModel?> create(modelClass: Class<viewModel>): viewModel {
            if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
                return RepresentativeViewModel(electionDao) as viewModel
            }
            throw IllegalArgumentException("viewModel error")
        }
    }
}

