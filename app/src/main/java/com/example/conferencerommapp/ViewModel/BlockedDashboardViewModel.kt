package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Blocked
import com.example.conferencerommapp.Repository.BlockDashboardRepository

class BlockedDashboardViewModel : ViewModel() {

    var mBlockDashboardRepository : BlockDashboardRepository? =null
    var mBlockedList : MutableLiveData<List<Blocked>>? = null

    fun getBlockedList(mContext: Context):MutableLiveData<List<Blocked>>{
        if (mBlockedList == null) {
            mBlockDashboardRepository = BlockDashboardRepository.getInstance()
        }
        mBlockedList = mBlockDashboardRepository!!.getBlockedList(mContext) as MutableLiveData<List<Blocked>>
        return mBlockedList!!
    }
}