package com.krnventures.covid_19_2

class sample {


    //mlat: (Double) -> Unit, mlong: (Double) -> Unit, mloc: (String) -> Unit
//    fun apiCall(@Nullable callback: TravelHistoryInterface?) {
//        Log.i("timed", "2")
//
//        val apiInterface = ApiInterface.create().getTravelHistory()
//        apiInterface.enqueue(
//            object : Callback<TravelHistoryListDTO> {
//                override fun onFailure(call: Call<TravelHistoryListDTO>, t: Throwable) {
//                    Log.i("hithere2", "failure")
//                    if (callback != null){
//                        callback.onError(t);
//                    }
//                }
//
//                override fun onResponse(
//                    call: Call<TravelHistoryListDTO>,
//                    response: Response<TravelHistoryListDTO>
//                ) {
//                    val travleLocations = response.body()
//                    Log.i("hithere", travleLocations.toString())
//
//                   // mTravelLocations.addAll(travleLocations!!.travel_history!!)
//                    //requiredDTO?.onSuccess(travleLocations)
//                    //this function won't work if called in onCreate because mTravlelocations will be null
//                    //hiThere(mTravelLocations)
//
//                    if (callback != null){
//                        callback.onSuccess(travleLocations);
//                    }
//
//                }
//
//            }
//        )
//    }




//    var dlat: Double = destinationLat
//    var dlong: Double = destinationLong
//    private var mLocationName: String = locationName
//
//    fun checkOnce() {
//        Log.i("paaniMe", dlat.toString() + " " + mLocationName  )
//    }

}