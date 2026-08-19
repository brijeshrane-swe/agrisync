package com.example.data.remote.api

import com.example.data.remote.dto.DcaDatasetItemDto
import com.example.data.remote.dto.DcaTriggerRequest
import com.example.data.remote.dto.DcaTriggerResponse
import com.example.data.remote.dto.SelfHealTriggerRequest
import com.example.data.remote.dto.SelfHealTriggerResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface BrightDataApi {

    @POST("dca/trigger")
    suspend fun triggerBatchScrape(
        @Header("Authorization") authHeader: String,
        @Query("collector") collectorId: String,
        @Body urls: List<DcaTriggerRequest>
    ): DcaTriggerResponse

    @GET("dca/dataset")
    suspend fun getDataset(
        @Header("Authorization") authHeader: String,
        @Query("id") collectionId: String
    ): List<DcaDatasetItemDto>

    @POST("dca/collectors/heal")
    suspend fun triggerSelfHeal(
        @Header("Authorization") authHeader: String,
        @Body request: SelfHealTriggerRequest
    ): SelfHealTriggerResponse
}
