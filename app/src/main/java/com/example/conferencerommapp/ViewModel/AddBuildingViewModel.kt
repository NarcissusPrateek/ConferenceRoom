package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.Repository.AddBuildingRepository

open class AddBuildingViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mAddBuildingRepository: AddBuildingRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccessForAddBuilding = MutableLiveData<Int>()
    var mFailureForAddBuilding = MutableLiveData<String>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addBuildingDetails(mAddBuilding: AddBuilding) {
        mAddBuildingRepository = AddBuildingRepository.getInstance()
        mAddBuildingRepository!!.addBuildingDetails(mAddBuilding, object : ResponseListener {
            override fun onFailure(failure: String) {
                mFailureForAddBuilding.value = failure
            }

            override fun onSuccess(success: Any) {
                mSuccessForAddBuilding.value = success as Int
            }
        })
    }

    fun returnSuccessForAddBuilding(): MutableLiveData<Int> {
        return mSuccessForAddBuilding
    }
    fun returnFailureForAddBuilding(): MutableLiveData<String> {
        return mFailureForAddBuilding
    }
}
