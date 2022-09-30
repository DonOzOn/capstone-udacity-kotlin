package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElections(elections: List<Election>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElection(election: Election): Long

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table")
    fun getAllElections(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table where isSaved = 1")
    fun getSavedElections(): LiveData<List<Election>>

    //TODO: Add select single election query
    @Query("SELECT * FROM election_table WHERE id=:id")
    fun getElectionById(id: Int): LiveData<Election>

    //TODO: Add delete query
    @Query("DELETE FROM election_table where id=:id")
    fun deleteElection(id: Int)

    //TODO: Add clear query

}