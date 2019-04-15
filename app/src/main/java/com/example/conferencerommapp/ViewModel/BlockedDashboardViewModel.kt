package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Blocked
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Helper.Unblock
import com.example.conferencerommapp.Repository.BlockDashboardRepository

class BlockedDashboardViewModel : ViewModel() {

    var mBlockDashboardRepository: BlockDashboardRepository? = null
    var mBlockedRoomList = MutableLiveData<List<Blocked>>()
    var mFailureCodeForBlockedRoomList = MutableLiveData<String>()
    var mSuccessCodeForBlockRoom =  MutableLiveData<Int>()
    var mFailureCodeForBlockRoom =  MutableLiveData<String>()

    fun getBlockedList() {
        mBlockDashboardRepository = BlockDashboardRepository.getInstance()
        mBlockDashboardRepository!!.getBlockedList(object : ResponseListener {
            override fun onSuccess(success: Any) {
                mBlockedRoomList.value = success as List<Blocked>
            }

            override fun onFailure(failure: String) {
                mFailureCodeForBlockedRoomList.value = failure
            }

        })

    }
    fun returnBlockedRoomList(): MutableLiveData<List<Blocked>> {
        return mBlockedRoomList
    }

    fun returnFailureCodeFromBlockedApi(): MutableLiveData<String> {
        return mFailureCodeForBlockedRoomList
    }
    //-----------------------------------------------------------------------------------------------------------

    fun unBlockRoom(mRoom: Unblock) {
        mBlockDashboardRepository = BlockDashboardRepository.getInstance()
        mBlockDashboardRepository!!.unblockRoom(mRoom, object: ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessCodeForBlockRoom.value = success as Int
            }

            override fun onFailure(failure: String) {
                mFailureCodeForBlockRoom.value = failure
            }

        })
    }

    fun returnSuccessCodeForUnBlockRoom(): MutableLiveData<Int> {
        return mSuccessCodeForBlockRoom
    }
    fun returnFailureCodeForUnBlockRoom(): MutableLiveData<String> {
        return mFailureCodeForBlockRoom
    }
}