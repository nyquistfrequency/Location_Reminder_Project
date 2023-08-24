package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTaskAndLoad() = runBlockingTest {
        // GIVEN - insert a reminder
        val reminderToBeSaved =
            ReminderDTO("Title 1", "Description 1", "Location 1", 10.000, -10.000)

        database.reminderDao().saveReminder(reminderToBeSaved)

        // WHEN - Get the reminder from the database
        val loaded = database.reminderDao().getReminders()

        val loadedDataInPosition = loaded[0]

        // THEN - The loaded data contains the expected values
        assertThat(loadedDataInPosition.id,`is`(reminderToBeSaved.id))
        assertThat(loadedDataInPosition.title,`is`(reminderToBeSaved.title))
        assertThat(loadedDataInPosition.description,`is`(reminderToBeSaved.description))
        assertThat(loadedDataInPosition.location,`is`(reminderToBeSaved.location))
        assertThat(loadedDataInPosition.latitude,`is`(reminderToBeSaved.latitude))
        assertThat(loadedDataInPosition.longitude,`is`(reminderToBeSaved.longitude))
    }

}