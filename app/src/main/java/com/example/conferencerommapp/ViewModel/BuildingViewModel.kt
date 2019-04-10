package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.Repository.BuildingsRepository

class BuildingViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mBuildingsRepository: BuildingsRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mBuildingList: MutableLiveData<List<Building>>? = null

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getBuildingList(): MutableLiveData<List<Building>> {
        if (mBuildingList == null) {
            mBuildingsRepository = BuildingsRepository.getInstance()
        }
        mBuildingList = mBuildingsRepository!!.getBuildingList() as MutableLiveData<List<Building>>
        return mBuildingList!!
    }
}