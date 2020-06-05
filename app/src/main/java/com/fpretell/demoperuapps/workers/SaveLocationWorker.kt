package com.fpretell.demoperuapps.workers

import android.content.Context
import android.location.Location
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fpretell.demoperuapps.models.Localizacion
import com.fpretell.demoperuapps.persistence.LocationEntity
import com.fpretell.demoperuapps.persistence.LocationsDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class SaveLocationWorker(
    ctx: Context,
    params: WorkerParameters
) : Worker(ctx, params) {

    override fun doWork(): Result {
        val locationDao = LocationsDatabase.getInstance(applicationContext).locationDao()
        val fusedLocationClient: FusedLocationProviderClient

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            val ubicacion = Localizacion(latitud = location?.latitude, longitud = location?.longitude)
            locationDao.insert(
                LocationEntity(
                    id = ubicacion.id,
                    lat = ubicacion.latitud!!,
                    lon = ubicacion.longitud!!
                )
            )
        }

        return Result.success()
    }
}