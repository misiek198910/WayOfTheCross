package droga_krzyzowa.droga_krzyzowa.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import droga_krzyzowa.droga_krzyzowa.data.dao.SubscriptionDao
import droga_krzyzowa.droga_krzyzowa.entity.SubscriptionEntity

// Definicja bazy danych łącząca Encję i Dao
@Database(entities = [SubscriptionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "way_of_the_cross_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}