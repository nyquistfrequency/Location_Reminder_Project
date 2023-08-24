package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        remindersLocalRepository =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_insertAndRetrieveData() = runBlocking {
        // GIVEN - insert a reminder
        val reminderToBeSaved =
            ReminderDTO("Title 1", "Description 1", "Location 1", 10.000, -10.000)
        remindersLocalRepository.saveReminder(reminderToBeSaved)

        // WHEN  - reminder retrieved by ID
        val result = remindersLocalRepository.getReminder(reminderToBeSaved.id)

        // THEN - Same reminder is returned
        assertThat(result is Result.Success, `is`(true))
        result as Result.Success

        assertThat(result.data.title, `is`(reminderToBeSaved.title))
        assertThat(result.data.description, `is`(reminderToBeSaved.description))
        assertThat(result.data.latitude, `is`(reminderToBeSaved.latitude))
        assertThat(result.data.longitude, `is`(reminderToBeSaved.longitude))
        assertThat(result.data.location, `is`(reminderToBeSaved.location))
    }

    @Test
    fun saveReminder_returnErrorIfEntryNotFound() = runBlocking {
        // GIVEN - insert a reminder
        val reminderToBeSaved =
            ReminderDTO("Title 1", "Description 1", "Location 1", 10.000, -10.000)

        // WHEN - saving the reminder
        remindersLocalRepository.saveReminder(reminderToBeSaved)

        // AND WHEN - deleting all reminders after saving
        remindersLocalRepository.deleteAllReminders()

        // THEN - the reminder shall not be found
        val result = remindersLocalRepository.getReminder(reminderToBeSaved.id)

        assertThat(result is Result.Error, `is`(true))
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))

    }
}