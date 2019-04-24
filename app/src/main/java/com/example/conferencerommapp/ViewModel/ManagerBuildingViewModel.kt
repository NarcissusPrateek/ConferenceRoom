package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.Repository.BuildingsRepository

class ManagerBuildingViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    private var mBuildingsRepository: BuildingsRepository? = null
    /**
     * MutableLiveData variables which will hold positive and negative response from server
     */
    var mBuildingList =  MutableLiveData<List<Building>>()

    var mFailureCode = MutableLiveData<String>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     */
    fun getBuildingList(userId: String, token: String) {
        mBuildingsRepository = BuildingsRepository.getInstance()
        mBuildingsRepository!!.getBuildingList(userId, token, object: ResponseListener {
            override fun onSuccess(success: Any) {
              mBuildingList.value = success as List<Building>
            }

            override fun onFailure(failure: String) {
                mFailureCode.value = failure
            }

        })
    }
    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnBuildingSuccess(): MutableLiveData<List<Building>> {
        return mBuildingList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnBuildingFailure(): MutableLiveData<String> {
        return mFailureCode
    }


}
