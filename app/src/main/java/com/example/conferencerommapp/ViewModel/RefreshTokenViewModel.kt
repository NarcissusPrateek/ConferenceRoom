package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.RefreshToken
import com.example.conferencerommapp.Repository.GetAccessTokenRepository

class RefreshTokenViewModel: ViewModel() {
    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mRefreshTokenRepository: GetAccessTokenRepository? = null

    /**
     * A MutableLivedata variable which will hold the Value for positive response from repository
     */
    var mToken = MutableLiveData<RefreshToken>()

    /**
     * A MutableLiveData variable which will hold the Value for negative response from repository
     */
    var errorCodeFromServerForToken =  MutableLiveData<String>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will update the values of MutableLiveData objects according to the response from server
     */
    fun getAccessAndRefreshToken(mRefreshToken: RefreshToken) {
        mRefreshTokenRepository = GetAccessTokenRepository.getInstance()
        mRefreshTokenRepository!!.getAccessToken(mRefreshToken, object: ResponseListener {
            override fun onFailure(failure: String) {
                errorCodeFromServerForToken.value = failure
            }
            override fun onSuccess(success: Any) {
                mToken.value = success as RefreshToken
            }

        })
    }

    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnAccessToken(): MutableLiveData<RefreshToken> {
        return mToken
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnAccessTokenFailure(): MutableLiveData<String> {
        return errorCodeFromServerForToken
    }
}
