package com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel

import com.jaehl.spaceTraders.data.model.MiningSurvey

data class MiningSurveyViewModel(
    val isVisible : Boolean = false,
    val canScan : Boolean = false,
    val surveyResults : List<MiningSurvey> = listOf()
)