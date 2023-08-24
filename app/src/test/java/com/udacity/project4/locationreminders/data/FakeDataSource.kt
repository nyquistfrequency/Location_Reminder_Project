package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var tasks: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

    private var returnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (returnError) {
            return Result.Error("Error: Reminders not found", 404)
        }

        tasks?.let { return Result.Success(it) }
        return Result.Error("OtherError")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        tasks?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val reminder = tasks?.find { it.id == id }

        return when {
            returnError -> {
                Result.Error("Reminder not found!")
            }
            reminder != null -> {
                Result.Success(reminder)
            }
            else -> {
                Result.Error("Reminder not found!")
            }
        }
    }

    override suspend fun deleteAllReminders() {
        tasks?.clear()
    }

    fun shouldReturnError(boolean: Boolean) {
        returnError = boolean
    }


}