package com.example.skismoring.ui.tips

import android.app.Application
import androidx.lifecycle.*
import com.example.skismoring.data.local.LocationRoomDatabase
import com.example.skismoring.data.manager.RecommendationRepository
import com.example.skismoring.data.model.Recommendation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TipsViewModel (application: Application): AndroidViewModel(application) {

    private val recommendationRepository: RecommendationRepository
    init {
        val waxDao = LocationRoomDatabase.getDatabase(application).waxDao()
        recommendationRepository = RecommendationRepository.getInstance(waxDao)
    }

    val optimalRecommendation : LiveData<Recommendation> get() = recommendationRepository.optimalGeneralRecommendation
    val personalRecommendation : LiveData<Recommendation> get() = recommendationRepository.personalGeneralRecommendation

    //Function that fetches the best wax based on the weather
    fun getTips(temperature: String, snowtype: String){
        viewModelScope.launch(Dispatchers.IO) {
            recommendationRepository.getGeneralRecommendation(temperature.toFloat(), snowtype)
        }
    }
}