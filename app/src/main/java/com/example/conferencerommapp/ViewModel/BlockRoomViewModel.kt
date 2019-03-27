package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.BlockRoom
import com.example.conferencerommapp.Repository.BlockRoomRepository

class BlockRoomViewModel : ViewModel() {
    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mBlockRoomRepository: BlockRoomRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mStatus: MutableLiveData<Int>? = null

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun blockRoom(mContext: Context, mRoom: BlockRoom): MutableLiveData<Int> {
        mBlockRoomRepository = BlockRoomRepository.getInstance()
        mStatus = mBlockRoomRepository!!.blockRoom(mContext, mRoom) as MutableLiveData<Int>
        return mStatus!!
    }
}