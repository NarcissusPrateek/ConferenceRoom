package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.Employee
import com.example.conferencerommapp.Repository.RegistrationRepository

class RegistrationViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mRegistrationRepository: RegistrationRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mStatus: MutableLiveData<Int>? = null

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addEmployee(mContext: Context, mEmployee: Employee): MutableLiveData<Int>? {
        mRegistrationRepository = RegistrationRepository.getInstance()
        mStatus = mRegistrationRepository!!.addEmployee(mContext, mEmployee) as MutableLiveData<Int>
        return mStatus
    }
}