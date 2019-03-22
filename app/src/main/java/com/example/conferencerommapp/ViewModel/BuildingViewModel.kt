package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.Repository.BuildingsRepository

class BuildingViewModel : ViewModel() {
    var mBuildingsRepository: BuildingsRepository? = null
    var mBuildingList: MutableLiveData<List<Building>>? = null
    fun getBuildingList(context: Context): MutableLiveData<List<Building>> {
        if (mBuildingList == null) {
            mBuildingsRepository = BuildingsRepository.getInstance()
        }
        mBuildingList = mBuildingsRepository!!.getBuildingList(context) as MutableLiveData<List<Building>>
        return mBuildingList!!
    }
}