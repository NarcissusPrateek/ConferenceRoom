package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.Unblock
import com.example.conferencerommapp.Repository.UnblockRoomRepository

class UnBlockRoomViewModel : ViewModel() {
    var mUnBlockRoomRepository:UnblockRoomRepository? = null
    var mStatus: MutableLiveData<Int>? = null

    fun unBlockRoom(mContext: Context, room: Unblock):MutableLiveData<Int>{
        mUnBlockRoomRepository = UnblockRoomRepository.getInstance()
        mStatus = mUnBlockRoomRepository!!.unblockRoom(mContext,room)
        return mStatus!!
    }
}