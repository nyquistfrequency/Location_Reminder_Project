package com.udacity.project4.utils

import android.content.Context
import com.google.android.gms.location.GeofenceStatusCodes
import com.udacity.project4.R

internal object GeofencingConstants {
    const val GEOFENCE_RADIUS_IN_METERS = 100f
    const val ACTION_GEOFENCE_EVENT =
        "locationreminders.geofence.action.ACTION_GEOFENCE_EVENT"
}

/**
 * Returns the error string for a geofencing error code.
 */
fun errorMessage(context: Context, errorCode: Int): String {
    val resources = context.resources
    return when (errorCode) {
        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> resources.getString(
            R.string.geofence_not_available
        )
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> resources.getString(
            R.string.geofence_too_many_geofences
        )
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> resources.getString(
            R.string.geofence_too_many_pending_intents
        )
        else -> resources.getString(R.string.geofence_unknown_error)
    }
}