package com.fpretell.demoperuapps.workers

import com.google.gson.annotations.SerializedName

private const val MIN_TIME_TO_SYNC = 30L
private const val MAX_TIME_TO_SYNC = 60L

data class WorkerTimeModel(
    @SerializedName("minTime") val minTimeToSync: Long = MIN_TIME_TO_SYNC,
    @SerializedName("maxTime") val maxTimeToSync: Long = MAX_TIME_TO_SYNC)