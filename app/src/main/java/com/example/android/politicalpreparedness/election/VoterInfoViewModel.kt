package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class VoterInfoViewModel(application: Application) : AndroidViewModel(application) {

    //TODO: Add live data to hold voter info
    private val database = ElectionDatabase.getInstance(application)
    private val electionsRepository = ElectionsRepository(database)

    val voterInfo = electionsRepository.voterInfo

    var Url = MutableLiveData<String>()

    private val electionId = MutableLiveData<Int>()
    val election = Transformations.switchMap(electionId) { id ->
            electionsRepository.getElection(id)
    }

    //TODO: Add var and methods to populate voter info
    fun getElection(id: Int) {
        electionId.value = id
    }

    fun getVoterInfo(electionId: Int, address: String) =
        viewModelScope.launch {
            electionsRepository.getVoterInfo(electionId, address)
        }

    //TODO: Add var and methods to support loading URLs
    fun intentUrl(url: String) {
        Url.value = url
    }

    //TODO: Add var and methods to save and remove elections to local database
    fun saveElection(election: Election) {
        election.isSaved = !election.isSaved
        viewModelScope.launch {
            electionsRepository.insertElection(election)
        }
    }
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}