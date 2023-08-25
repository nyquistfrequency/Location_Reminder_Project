package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var tasks: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

    private var returnError = false
    private lateinit var remindersDao: RemindersDao

    /**
     * Get the reminders list from the local db
     * @return Result the holds a Success with all the reminders or an Error object with the error message
     */
    override suspend fun getReminders(): Result<List<ReminderDTO>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(remindersDao.getReminders())
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        tasks?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> = withContext(Dispatchers.IO) {
        try {
            val reminder = remindersDao.getReminderById(id)
            if (reminder != null) {
                return@withContext Result.Success(reminder)
            } else {
                return@withContext Result.Error("Reminder not found!")
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e.localizedMessage)
        }
    }

    override suspend fun deleteAllReminders() {
        tasks?.clear()
    }

    fun shouldReturnError(boolean: Boolean) {
        returnError = boolean
    }


}