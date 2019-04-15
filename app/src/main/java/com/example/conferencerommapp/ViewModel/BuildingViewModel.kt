package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.Repository.BuildingsRepository

class BuildingViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mBuildingsRepository: BuildingsRepository? = null

    /**
     * A MutableLivedata variable which will hold the Value for positive response from repository
     */
    var mBuildingList =  MutableLiveData<List<Building>>()

    /**
     * A MutableLiveData variable which will hold the Value for negative response from repository
     */
    var errorCodeFromServer =  MutableLiveData<Int>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will update the values of MutableLiveData objects according to the response from server
     */
    fun getBuildingList() {
        mBuildingsRepository = BuildingsRepository.getInstance()
        mBuildingsRepository!!.getBuildingList(object: ResponseListener {
            override fun onFailure(failure: Int) {
                    errorCodeFromServer.value = failure
            }
            override fun onSuccess(success: Any) {
                mBuildingList!!.value = success as List<Building>
            }

        })
    }

    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnMBuildingSuccess(): MutableLiveData<List<Building>> {
        return mBuildingList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnMBuildingFailure(): MutableLiveData<Int> {
        return errorCodeFromServer
    }
}