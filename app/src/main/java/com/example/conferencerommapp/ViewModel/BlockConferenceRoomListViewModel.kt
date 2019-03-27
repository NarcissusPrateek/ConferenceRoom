package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.BuildingConference
import com.example.conferencerommapp.Repository.BlockConferenceRoomListRepository

class BlockConferenceRoomListViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mBlockConferenceRoomListRepository: BlockConferenceRoomListRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mConferenceRoomList: MutableLiveData<List<BuildingConference>>? = null


    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getRoomList(mContext: Context, buildingId: Int): MutableLiveData<List<BuildingConference>>? {
        mBlockConferenceRoomListRepository = BlockConferenceRoomListRepository.getInstane()
        mConferenceRoomList = mBlockConferenceRoomListRepository!!.getRoomList(
            mContext,
            buildingId
        ) as MutableLiveData<List<BuildingConference>>
        return mConferenceRoomList
    }
}