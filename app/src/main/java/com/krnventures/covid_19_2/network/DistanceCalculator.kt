package com.krnventures.covid_19_2.network

class DistanceCalculator()  {

    @Throws(Exception::class)
    fun main(args: Array<String>) {
        println(distance(29.8649, 77.8965, 28.5450, 77.1926, "M").toString() + " Miles\n")
        println(distance(29.8649, 77.8965, 28.5450, 77.1926, "K").toString() + " Kilometers\n")
        println(
            distance(
                32.9697,
                -96.80322,
                29.46786,
                -98.53506,
                "N"
            ).toString() + " Nautical Miles\n"
        )
    }

    private fun distance (
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        unit: String
    ): Double {
        return if (lat1 == lat2 && lon1 == lon2) {
            0.0
        } else {
            val theta = lon1 - lon2
            var dist =
                Math.sin(Math.toRadians(lat1)) * Math.sin(
                    Math.toRadians(lat2)
                ) + Math.cos(Math.toRadians(lat1)) * Math.cos(
                    Math.toRadians(
                        lat2
                    )
                ) * Math.cos(Math.toRadians(theta))
            dist = Math.acos(dist)
            dist = Math.toDegrees(dist)
            dist = dist * 60 * 1.1515
            if (unit == "K") {
                dist = dist * 1.609344
            } else if (unit == "N") {
                dist = dist * 0.8684
            }
            dist
        }
    }

}






