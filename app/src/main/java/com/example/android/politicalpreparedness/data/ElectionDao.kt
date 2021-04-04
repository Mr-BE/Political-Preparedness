package com.example.android.politicalpreparedness.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //COMPLETED: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveElection(election: Election)

    //COMPLETED: Add select single election query
    @Query("SELECT * FROM election_table where id = :electionId")
    suspend fun getElectionById(electionId: String): Election?


    //COMPLETED: Add select all election query
    @Query("SELECT * FROM election_table")
    fun getElections(): LiveData<List<Election>?>

    //COMPLETED: Add delete query
    @Query("DELETE FROM election_table where electionDay =:day")
    suspend fun deleteElectionByDay(day: String)

    //COMPLETED: Add clear query
    @Query("DELETE FROM election_table")
    suspend fun deleteAllElections()

}