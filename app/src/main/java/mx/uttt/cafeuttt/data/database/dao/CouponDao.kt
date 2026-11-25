package mx.uttt.cafeuttt.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mx.uttt.cafeuttt.data.database.entity.CouponEntity

@Dao
interface CouponDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: CouponEntity)
    @Update suspend fun update(item: CouponEntity)
    @Delete suspend fun delete(item: CouponEntity)
    @Query("SELECT * FROM coupons ORDER BY code") fun observeAll(): Flow<List<CouponEntity>>
    @Query("SELECT * FROM coupons WHERE code = :code AND active = 1 LIMIT 1") fun findActive(code: String): Flow<CouponEntity?>
}
