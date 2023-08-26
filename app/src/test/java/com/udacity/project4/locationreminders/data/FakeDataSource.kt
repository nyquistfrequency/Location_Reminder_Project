package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
//Note: After feedback I did a change here from MutableList<ReminderDTO>? to MutableList<ReminderDTO>
//"Please note that if no reminders are found, then Room simply returns an empty list. It will never return null.
//Hence, the data that the fake data source holds should be declared as non-nullable."
class FakeDataSource(var tasks: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

    private var returnError = false

    fun shouldReturnError(boolean: Boolean) {
        returnError = boolean
    }

    /**
     * Get the reminders list from the local db
     * @return Result the holds a Success with all the reminders or an Error object with the error message
     */
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
            if (returnError) {
                return Result.Error("Test throws exception")
            }
        return Result.Success(ArrayList(tasks))
    }


    override suspend fun saveReminder(reminder: ReminderDTO) {
        tasks.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (returnError)
            return Result.Error("Test throws exception")

        tasks.firstOrNull { it.id == id }?.let {
            return Result.Success(it)
        }
        return Result.Error("Reminder not found!")
    }

    override suspend fun deleteAllReminders() {
        tasks.clear()
    }

}