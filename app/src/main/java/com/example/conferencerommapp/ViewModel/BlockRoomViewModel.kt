package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.BuildingConference
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.BlockRoom
import com.example.conferencerommapp.Model.BlockingConfirmation
import com.example.conferencerommapp.Repository.BlockRoomRepository


class BlockRoomViewModel : ViewModel() {
    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    private var mBlockRoomRepository: BlockRoomRepository? = null

    /**
     * a MutableLiveData variable which will hold the positive response from server for the list of rooms
     */
    var mConferenceRoomList =  MutableLiveData<List<BuildingConference>>()

    /**
     * a MutableLiveData variable which will hold the for positive response from server for the confirmation of blocking
     */
    var mConfirmation =  MutableLiveData<BlockingConfirmation>()

    /**
     * a variable to hold positive response from backend for blocking room
     */
    var mSuccessForBlockRoom =  MutableLiveData<Int>()

    /**
     * a variable to hold failure code from backend whenever unable to fetch the list of room from server
     */
    var mFailureCodeForRoom = MutableLiveData<String>()

    /**
     * a variable to hold failure code from backend whenever unable to fetch the confirmation details from server
     */
    var mFailureCodeForConfirmationOfBlocking = MutableLiveData<String>()

    /**
     * a variable to hold failure code from backend whenever unable to block the room
     */
    var mFailureCodeForBlockRoom = MutableLiveData<String>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun blockRoom(mRoom: BlockRoom, userId: String, token: String) {
        mBlockRoomRepository = BlockRoomRepository.getInstance()
        mBlockRoomRepository!!.blockRoom(mRoom, userId, token,object: ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessForBlockRoom.value = success as Int
            }

            override fun onFailure(failure: String) {
                mFailureCodeForBlockRoom.value = failure
            }

        })
    }

    fun returnSuccessForBlockRoom(): MutableLiveData<Int> {
        return mSuccessForBlockRoom
    }

    fun returnResponseErrorForBlockRoom():MutableLiveData<String> {
        return mFailureCodeForBlockRoom
    }

    /**
     * ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getRoomList(buildingId: Int, userId: String, token: String) {
        mBlockRoomRepository = BlockRoomRepository.getInstance()
        mBlockRoomRepository!!.getRoomList(buildingId, userId, token, object: ResponseListener {
            override fun onSuccess(success: Any) {
                mConferenceRoomList.value = success as List<BuildingConference>
            }
            override fun onFailure(failure: String) {
                mFailureCodeForRoom.value = failure
            }

        })
    }

    fun returnConferenceRoomList(): MutableLiveData<List<BuildingConference>> {
        return mConferenceRoomList
    }

    fun returnResponseErrorForConferenceRoom():MutableLiveData<String> {
        return mFailureCodeForRoom
    }

    /**
     * ----------------------------------------------------------------------------------------------------------------
     */



    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun blockingStatus(mRoom: BlockRoom, userId: String, token: String) {
        mBlockRoomRepository = BlockRoomRepository.getInstance()
        mBlockRoomRepository!!.blockingStatus(mRoom, userId, token, object: ResponseListener {
            override fun onSuccess(success: Any) {
                mConfirmation.value = success as BlockingConfirmation
            }

            override fun onFailure(failure: String) {
                mFailureCodeForConfirmationOfBlocking.value = failure
            }

        })
    }

    fun returnSuccessForConfirmation(): MutableLiveData<BlockingConfirmation> {
        return mConfirmation
    }

    fun returnResponseErrorForConfirmation():MutableLiveData<String> {
        return mFailureCodeForConfirmationOfBlocking
    }



}