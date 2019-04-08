package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.Repository.AddBuildingRepository

class AddBuildingViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mAddBuildingRepository: AddBuildingRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mStatus: MutableLiveData<Int>? = null


    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addBuildingDetails(mContext: Context, mAddBuilding: AddBuilding): MutableLiveData<Int>? {
        mAddBuildingRepository = AddBuildingRepository.getInstance()
        mStatus = mAddBuildingRepository!!.addBuildingDetails(mContext, mAddBuilding) as MutableLiveData<Int>
        return mStatus
    }
    fun validateTime(a: Int):Int {
        return a
    }
}
