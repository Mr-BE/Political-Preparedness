package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class CivicRepository(private val electionDao: ElectionDao) {

    val electionList: LiveData<List<Election>?> = electionDao.getElections()
    val savedElectionList: LiveData<List<Election>?> = electionDao.getSavedElections()


    //refresh election content
    suspend fun refreshElection() {
        try {
            withContext(Dispatchers.IO) {
                Timber.d("Election network refresh called")
                val networkResult = CivicsApi.retrofitService.getElections().await()
                electionDao.retrieveElections(networkResult.elections)
                Timber.d("Election network refresh. value -> $networkResult")
            }
        } catch (error: Exception) {
            Timber.e("Election network refresh failed. Reason -> ${error.message}")
        }
    }
}