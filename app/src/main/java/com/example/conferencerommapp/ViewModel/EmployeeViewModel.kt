package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Repository.BuildingsRepository
import com.example.conferencerommapp.Repository.EmployeeRepository

class EmployeeViewModel : ViewModel() {
    var mEmployeeRepository: EmployeeRepository? = null
    var mEmployeeList: MutableLiveData<List<EmployeeList>>? = null

    fun getEmployeeList(): LiveData<List<EmployeeList>> {
        if (mEmployeeList == null) {
            mEmployeeRepository = EmployeeRepository.getInstance()
            mEmployeeList = mEmployeeRepository!!.getEmployeeList() as MutableLiveData<List<EmployeeList>>
        }
        return mEmployeeList!!
    }
}