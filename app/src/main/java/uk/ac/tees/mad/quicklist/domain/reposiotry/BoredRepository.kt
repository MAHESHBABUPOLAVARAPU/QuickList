package uk.ac.tees.mad.quicklist.domain.reposiotry

import uk.ac.tees.mad.quicklist.data.remote.api.activityDto.ActivityDtoItem

interface BoredRepository  {

    suspend fun getRandomActivity(type: String) : List<ActivityDtoItem>

}