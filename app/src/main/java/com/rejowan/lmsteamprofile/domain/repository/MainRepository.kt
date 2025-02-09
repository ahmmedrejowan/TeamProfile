package com.rejowan.lmsteamprofile.domain.repository

import androidx.lifecycle.LiveData
import com.rejowan.lmsteamprofile.data.remote.response.AllRoundedResponse
import com.rejowan.lmsteamprofile.data.remote.response.BatterResponse
import com.rejowan.lmsteamprofile.data.remote.response.BowlerResponse
import com.rejowan.lmsteamprofile.data.remote.response.TeamProfileData

interface MainRepository {

    val teamProfileData : LiveData<TeamProfileData>

    val battersList: LiveData<MutableList<BatterResponse>>

    val bowlerList: LiveData<MutableList<BowlerResponse>>

    val allRounderList: LiveData<MutableList<AllRoundedResponse>>


    suspend fun getSummary()

    suspend fun getBatters()

    suspend fun loadMoreBatters()

    suspend fun getBowlers()

    suspend fun loadMoreBowlers()

    suspend fun getAllRounders()




}