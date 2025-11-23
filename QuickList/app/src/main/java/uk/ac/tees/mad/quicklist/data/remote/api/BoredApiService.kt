package uk.ac.tees.mad.quicklist.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Query

import uk.ac.tees.mad.quicklist.data.remote.api.activityDto.ActivityDtoItem

interface BoredApiService {


    @GET("filter")
    suspend fun getRandomActivity( @Query("type") type: String): List<ActivityDtoItem>


}