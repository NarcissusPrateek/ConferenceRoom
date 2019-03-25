package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Repository.EmployeeRepository

class EmployeeViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mEmployeeRepository: EmployeeRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mEmployeeList: MutableLiveData<List<EmployeeList>>? = null

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getEmployeeList(mContext: Context): MutableLiveData<List<EmployeeList>> {
        if (mEmployeeList == null) {
            mEmployeeRepository = EmployeeRepository.getInstance()
            mEmployeeList = mEmployeeRepository!!.getEmployeeList(mContext) as MutableLiveData<List<EmployeeList>>
        }
        return mEmployeeList!!
    }
}
