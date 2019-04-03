package com.example.conferencerommapp.services

import com.example.conferencerommapp.Blocked
import com.example.conferencerommapp.BuildingConference
import com.example.conferencerommapp.BuildingT
import com.example.conferencerommapp.Helper.Unblock
import com.example.conferencerommapp.Model.*
import com.example.conferencerommapp.AddConferenceRoom
import com.example.myapplication.Models.ConferenceList
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ConferenceService {

    @GET("api/Building")
    fun getBuildingList(): Call<List<Building>>

    @POST("api/RoomsWithStatus")
    fun getConferenceRoomList(@Body availableRoom: FetchConferenceRoom): Call<List<ConferenceRoom>>

    @GET("api/UserLogin")
    fun getRequestCode(@Query("email") email: String?): Call<Int>

    @GET("api/Dashboard")
    fun getDashboard(@Query("email") email: String?): Call<List<Dashboard>>

    @POST("api/UserLogin")
    fun addEmployee(@Body newEmoployee: Employee): Call<ResponseBody>

    @POST("api/Booking")
    fun addBookingDetails(@Body booking: Booking): Call<ResponseBody>

    @POST("api/CancelBooking")
    fun cancelBooking(@Body cancel: CancelBooking): Call<ResponseBody>

    @GET("api/Employee")
    fun getEmployees(): Call<List<EmployeeList>>

    @POST("api/RecurringMeeting")
    fun addManagerBookingDetails(@Body managerBooking: ManagerBooking): Call<ResponseBody>

    @POST("api/ListOfAvailableRooms")
    fun getMangerConferenceRoomList(@Body availableRoom: ManagerConference): Call<List<ConferenceRoom>>
//    // Pratheek's.....

    @POST("api/Building")
    fun addBuilding(@Body newBuilding: AddBuilding): Call<ResponseBody>

    @POST("api/conference")
    fun addConference(@Body newConferenceRoom: AddConferenceRoom): Call<ResponseBody>

    @POST("api/BlockConfirmation")
    fun blockConfirmation(@Body room: BlockRoom): Call<BlockingConfirmation>

    @POST("api/blocking")
    fun blockconference(@Body room: BlockRoom): Call<ResponseBody>

    @GET("api/Building")
    fun getBuildings(): Call<List<BuildingT>>

    @GET("api/Building/{id}")
    fun getBuildingsConference(@Path("id") id: Int): Call<List<BuildingConference>>

    @GET("api/blocking")
    fun getBlockedConference(): Call<List<Blocked>>

    @POST("api/unblocking")
    fun unBlockingConferenceRoom(@Body room: Unblock): Call<ResponseBody>

    @GET("api/conference/{id}")
    fun conferencelist(@Query("buildingId") id: Int): Call<List<ConferenceList>>

    @PUT("api/Booking")
    fun update(@Body updateBooking: UpdateBooking):Call<ResponseBody>

}