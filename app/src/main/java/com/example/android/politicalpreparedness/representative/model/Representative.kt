package com.example.android.politicalpreparedness.representative.model

import com.example.android.politicalpreparedness.network.models.Office
import com.example.android.politicalpreparedness.network.models.Official
import java.io.Serializable

data class Representative (
        val official: Official,
        val office: Office
)