package com.example.conferencerommapp.ViewModel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.Repository.AddBuildingRepository

class AddBuildingViewModel : ViewModel() {
    var mAddBuildingRepository: AddBuildingRepository? = null
    var mStatus: MutableLiveData<Int>? = null

    fun addBuildingDetails(mContext:Context, mAddBuilding:AddBuilding): MutableLiveData<Int>? {
        mAddBuildingRepository = AddBuildingRepository.getInstance()
        mStatus = mAddBuildingRepository!!.addBuildingDetails(mContext,mAddBuilding) as MutableLiveData<Int>
        return mStatus
    }
}