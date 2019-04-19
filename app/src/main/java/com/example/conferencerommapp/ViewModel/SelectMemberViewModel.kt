package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Repository.EmployeeRepository

class SelectMemberViewModel: ViewModel() {
    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    private var mEmployeeRepository: EmployeeRepository? = null

    /**
     * a MutableLivedata variable which will hold the response from server
     */
    var mEmployeeList =  MutableLiveData<List<EmployeeList>>()

    var mErrorCodeFromServerForEmployees =  MutableLiveData<String>()

    /**
     * for Employee List
     */
    fun getEmployeeList() {
        mEmployeeRepository = EmployeeRepository.getInstance()
        mEmployeeRepository!!.getEmployeeList(object: ResponseListener {
            override fun onFailure(failure: String) {
                mErrorCodeFromServerForEmployees.value = failure
            }
            override fun onSuccess(success: Any) {
                mEmployeeList.value = success as List<EmployeeList>
            }

        })

    }
    fun returnSuccessForEmployeeList(): MutableLiveData<List<EmployeeList>> {
        return mEmployeeList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForEmployeeList(): MutableLiveData<String> {
        return mErrorCodeFromServerForEmployees
    }

}