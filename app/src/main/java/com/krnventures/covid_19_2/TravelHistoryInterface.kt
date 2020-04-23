package com.krnventures.covid_19_2

import androidx.annotation.NonNull
import com.krnventures.covid_19_2.dto.TravelHistoryListDTO


interface TravelHistoryInterface {
    fun onSuccess(@NonNull value: TravelHistoryListDTO?)

    fun onError(@NonNull throwable: Throwable)
}