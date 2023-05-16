package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.MiningSurvey

data class ShipMiningSurveyResponse(
    val cooldown : Cooldown = Cooldown(),
    val surveys : List<MiningSurvey> = listOf()
)
