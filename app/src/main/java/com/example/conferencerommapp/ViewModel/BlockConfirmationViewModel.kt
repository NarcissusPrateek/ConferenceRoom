package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.BlockRoom
import com.example.conferencerommapp.Model.BlockingConfirmation
import com.example.conferencerommapp.Repository.BlockConfirmationRepository

class BlockConfirmationViewModel : ViewModel() {
    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mBlockConfirmationRepository: BlockConfirmationRepository? = null
    var mStatus: MutableLiveData<Int>? = null
    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mConfirmation: MutableLiveData<BlockingConfirmation>? = null


    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun blockingStatus(mContext: Context, room: BlockRoom): LiveData<BlockingConfirmation>? {
        mBlockConfirmationRepository = BlockConfirmationRepository.getInstance()
        mConfirmation =
            mBlockConfirmationRepository!!.blockingStatus(mContext, room) as MutableLiveData<BlockingConfirmation>
        return mConfirmation
    }
}