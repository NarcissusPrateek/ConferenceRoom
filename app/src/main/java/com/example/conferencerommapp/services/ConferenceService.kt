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
    fun getBuildingList(
        @Header("Token") token: String,
        @Header("UserId") userId: String
    ): Call<List<Building>>

    @POST("api/RoomsWithStatus")
    fun getConferenceRoomList(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body availableRoom: FetchConferenceRoom
    ): Call<List<ConferenceRoom>>

    @GET("api/UserLogin")
    fun getRequestCode(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Query("email") email: String?
    ): Call<Int>

    @GET("api/Dashboard")
    fun getDashboard(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Query("email") email: String?
    ): Call<List<Dashboard>>

    @POST("api/UserLogin")
    fun addEmployee(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body newEmoployee: Employee
    ): Call<ResponseBody>

    @POST("api/Booking")
    fun addBookingDetails(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body booking: Booking
    ): Call<ResponseBody>

    @POST("api/CancelBooking")
    fun cancelBooking(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body cancel: CancelBooking
    ): Call<ResponseBody>

    @GET("api/Employee")
    fun getEmployees(
        @Header("Token") token: String,
        @Header("UserId") userId: String
    ): Call<List<EmployeeList>>

    @POST("api/RecurringMeeting")
    fun addManagerBookingDetails(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body managerBooking: ManagerBooking
    ): Call<ResponseBody>

    @POST("api/ListOfAvailableRooms")
    fun getMangerConferenceRoomList(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body availableRoom: ManagerConference
    ): Call<List<ConferenceRoom>>
//    // Pratheek's.....

    @POST("api/Building")
    fun addBuilding(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body newBuilding: AddBuilding
    ): Call<ResponseBody>

    @POST("api/conference")
    fun addConference(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body newConferenceRoom: AddConferenceRoom
    ): Call<ResponseBody>

    @POST("api/BlockConfirmation")
    fun blockConfirmation(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body room: BlockRoom
    ): Call<BlockingConfirmation>

    @POST("api/blocking")
    fun blockconference(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body room: BlockRoom
    ): Call<ResponseBody>


    @GET("api/Building/{id}")
    fun getBuildingsConference(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Path("id") id: Int
    ): Call<List<BuildingConference>>

    @GET("api/blocking")
    fun getBlockedConference(
        @Header("Token") token: String,
        @Header("UserId") userId: String
    ): Call<List<Blocked>>

    @POST("api/unblocking")
    fun unBlockingConferenceRoom(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body room: Unblock
    ): Call<ResponseBody>

    @GET("api/conference/{id}")
    fun conferencelist(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Query("buildingId") id: Int
    ): Call<List<ConferenceList>>

    @PUT("api/Booking")
    fun update(
        @Header("Token") token: String,
        @Header("UserId") userId: String,
        @Body updateBooking: UpdateBooking
    ):Call<ResponseBody>
}