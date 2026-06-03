package droga_krzyzowa.droga_krzyzowa.data.dao

import androidx.room.*
import droga_krzyzowa.droga_krzyzowa.entity.SubscriptionEntity

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscription_status WHERE id = 1")
    suspend fun getStatus(): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(status: SubscriptionEntity)
}