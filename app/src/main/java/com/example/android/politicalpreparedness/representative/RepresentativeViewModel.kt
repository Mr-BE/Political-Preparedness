package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.ElectionDao
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.repository.CivicRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

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


    //    17939 KIETH HARROW BLVD STE 106,HOUSTON, TX 77084-5724
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
            _foundRepsResponse.value = _userAddress.value?.let { repo.getReps(it) }
            Timber.i("Address is ${_userAddress.value}")
        }
        val offices = _foundRepsResponse.value?.offices
        Timber.i("Number of offices is ${offices?.size}")
        val officials = _foundRepsResponse.value?.officials
        Timber.i("Number of offices is ${officials?.size}")
        _representatives.value = offices?.flatMap { office -> office.getRepresentatives(officials!!) }
        Timber.i("Number of reps is ${_representatives.value?.size}")
    }


    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields


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

