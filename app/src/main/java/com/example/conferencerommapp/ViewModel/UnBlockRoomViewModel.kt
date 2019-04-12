package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Activity.BlockedDashboard
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Helper.Unblock
import com.example.conferencerommapp.Repository.BlockDashboardRepository
import com.example.conferencerommapp.Repository.UnblockRoomRepository

class UnBlockRoomViewModel : ViewModel() {
    var mUnBlockDashBoardRepository: BlockDashboardRepository? = null
    var mSuccessCodeForBlockRoom =  MutableLiveData<Int>()
    var mFailureCodeForBlockRoom =  MutableLiveData<Int>()

    fun unBlockRoom(mRoom: Unblock) {
        mUnBlockDashBoardRepository = BlockDashboardRepository.getInstance()
        mUnBlockDashBoardRepository!!.unblockRoom(mRoom, object: ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessCodeForBlockRoom.value = success as Int
            }

            override fun onFailure(failure: Int) {
                mFailureCodeForBlockRoom.value = failure
            }

        })
    }

    fun returnSuccessCodeForBlockRoom(): MutableLiveData<Int> {
        return mSuccessCodeForBlockRoom
    }
    fun returnFailureCodeForBlockRoom(): MutableLiveData<Int> {
        return mFailureCodeForBlockRoom
    }
}