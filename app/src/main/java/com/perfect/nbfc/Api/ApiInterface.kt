package com.perfect.nbfc.Api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("AgentLogin")
    fun getLogin(@Body body: RequestBody):Call<String>

    @POST("ModuleList")
    fun getLoginLogin(@Body body: RequestBody):Call<String>

    @POST("CustomerSerachDetails")
    fun getCustomerSerachDetails(@Body body: RequestBody):Call<String>

    @POST("OTPVerification")
    fun getOTPVerification(@Body body: RequestBody):Call<String>

    @POST("AgentLogin")
    fun getOTP(@Body body: RequestBody): Call<String>

    @POST("AgentLogin")
    fun mpinLogin(@Body body: RequestBody): Call<String>

    @POST("TransactionRequest")
    fun getTransactionRequest(@Body body: RequestBody): Call<String>

    @POST("GroupLoanCollection")
    fun doGroupLoanCollection(@Body body: RequestBody): Call<String>

    @POST("AgentChangeCredentials")
    fun getChangeCredentials(@Body body: RequestBody):Call<String>

    @POST("CustomerSync")
    fun getOfflineAccounts(@Body body: RequestBody):Call<String>

    @POST("VerificationCall")
    fun getVerificationCall(@Body body: RequestBody): Call<String>

    @POST("AgentBalance")
    fun getAgentBalance(@Body body: RequestBody): Call<String>

    @POST("TransactionSync")
    fun getTransactionSync(@Body body: RequestBody): Call<String>

    @POST("AgentSummary")
    fun getAgentSummary(@Body body: RequestBody): Call<String>

    @POST("AccountFetching")
    fun getAccountfetch(@Body body: RequestBody): Call<String>

    @POST("BalanceInquiry")
    fun getBalenq(@Body body: RequestBody): Call<String>

    @POST("BalanceEnquirySplitupList")
    fun getbalsplit(@Body body: RequestBody): Call<String>

    @POST("CustomerSearchTransactionDetails")
    fun getTransactionhistory(@Body body: RequestBody): Call<String>

    @POST("TodoList")
    fun getTodoListing(@Body body: RequestBody): Call<String>

    @POST("AgentCollectionList")
    fun getAgentCollectionList(@Body body: RequestBody): Call<String>

    @POST("Demandlist")
    fun getDemandlist(@Body body: RequestBody): Call<String>

    @POST("MiniStatement")
    fun getMiniStatement(@Body body: RequestBody): Call<String>

    @POST("CollectionRemark")
    fun getCollectionRemark(@Body body: RequestBody): Call<String>
}

