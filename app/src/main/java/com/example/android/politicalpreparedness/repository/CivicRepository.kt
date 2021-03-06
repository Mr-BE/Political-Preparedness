package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.data.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class CivicRepository(private val electionDao: ElectionDao) {

    val electionList: LiveData<List<Election>?> = electionDao.getElections()
    val savedElectionList: LiveData<List<Election>?> = electionDao.getSavedElections()


    //voter info
    var voterInfo = MutableLiveData<VoterInfoResponse>()
    var reps = MutableLiveData<RepresentativeResponse?>()
    var isLoading = MutableLiveData<Boolean>()

    //refresh election content
    suspend fun refreshElection() {
        try {
            withContext(Dispatchers.IO) {
                Timber.d("Election network refresh called")
                val networkResult = CivicsApi.retrofitService.getElectionsAsync()

//                if (networkResult.isActive) {
//                    isLoading.postValue(true)
//                }

                electionDao.retrieveElections(networkResult.await().elections)
                Timber.d("Election network refresh. value -> $networkResult")
            }
        } catch (error: Exception) {
            Timber.e("Election network refresh failed. Reason -> ${error.message}")
        }
    }

    //Get specific election object
//    suspend fun getElectionById(electionId: Int): Election? {
//        return electionDao.getElectionById(electionId)
//    }

    //update election to indicate if saved or not
    suspend fun updateElection(election: Election) {
        withContext(Dispatchers.IO) {
            electionDao.updateElection(election)
        }

    }

    //get voter info
    suspend fun getVoterInfo(address: String, electionId: Int): VoterInfoResponse? {

        try {
            withContext(Dispatchers.IO) {
                val response = CivicsApi.retrofitService
                        .getVoterInfoAsync(address, electionId)
                if (response.isActive) {
                    isLoading.postValue(true)
                }
                voterInfo.postValue(response.await())

                return@withContext voterInfo.value
            }
        } catch (error: Exception) {
            Timber.e("Error getting voter info: Reason -> ${error.message}")
        }
        return voterInfo.value
    }

    //get representatives
    suspend fun getReps(address: String): RepresentativeResponse? {
        try {
            withContext(Dispatchers.IO) {
                val response = CivicsApi.retrofitService
                        .getRepresentativesAsync(address).await()
                reps.postValue(response)
                return@withContext reps.value
            }
        } catch (error: Exception) {
            Timber.e("Error getting reps: Reason -> ${error.message}")
        }
        return reps.value

    }

}