package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.Booking
import com.example.conferencerommapp.Model.Employee
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Repository.BookingRepository
import com.example.conferencerommapp.Repository.EmployeeRepository

class BookingViewModel: ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    private var mBookingRepository: BookingRepository? = null

    private var mEmployeeRepository: EmployeeRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccessForBooking =  MutableLiveData<Int>()

    var mErrorCodeFromServerFromBooking =  MutableLiveData<Int>()

    var mEmployeeList =  MutableLiveData<List<EmployeeList>>()

    var mErrorCodeFromServerForEmployees =  MutableLiveData<Int>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addBookingDetails(mBooking: Booking) {
        mBookingRepository = BookingRepository.getInstance()
        mBookingRepository!!.addBookingDetails(mBooking, object: ResponseListener {
            override fun onFailure(failure: Int) {
                mErrorCodeFromServerFromBooking.value = failure
            }
            override fun onSuccess(success: Any) {
                mSuccessForBooking.value = success as Int
            }

        })
    }

    /**
     * for Employee List
     */
    fun getEmployeeList() {
        mEmployeeRepository = EmployeeRepository.getInstance()
        mEmployeeRepository!!.getEmployeeList(object: ResponseListener {
            override fun onFailure(failure: Int) {
                mErrorCodeFromServerForEmployees.value = failure
            }
            override fun onSuccess(success: Any) {
                mEmployeeList.value = success as List<EmployeeList>
            }

        })

    }



    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnSuccessForBooking(): MutableLiveData<Int> {
        return mSuccessForBooking
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForBooking(): MutableLiveData<Int> {
        return mErrorCodeFromServerFromBooking
    }

    fun returnSuccessForEmployeeList(): MutableLiveData<List<EmployeeList>> {
        return mEmployeeList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForEmployeeList(): MutableLiveData<Int> {
        return mErrorCodeFromServerForEmployees
    }
}