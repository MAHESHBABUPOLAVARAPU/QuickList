package uk.ac.tees.mad.quicklist.data

import uk.ac.tees.mad.quicklist.data.remote.api.BoredApiService
import uk.ac.tees.mad.quicklist.data.remote.api.activityDto.ActivityDtoItem
import uk.ac.tees.mad.quicklist.domain.reposiotry.BoredRepository




class BoredRepositoryImpl (private val apiService: BoredApiService,) : BoredRepository {

    override suspend fun getRandomActivity(type: String): List<ActivityDtoItem> {
      return  apiService.getRandomActivity(type)
    }


}