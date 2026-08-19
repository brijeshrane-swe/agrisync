package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entity.CommodityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommodityDao {

    @Query("SELECT * FROM commodities ORDER BY lastUpdated DESC")
    fun getAllCommodities(): Flow<List<CommodityEntity>>

    @Query("SELECT * FROM commodities WHERE id = :id LIMIT 1")
    suspend fun getCommodityById(id: String): CommodityEntity?

    @Query("SELECT * FROM commodities WHERE state = :state ORDER BY modalPrice DESC")
    fun getCommoditiesByState(state: String): Flow<List<CommodityEntity>>

    @Query("""
        SELECT * FROM commodities 
        WHERE commodityName LIKE '%' || :query || '%' 
           OR marketCentre LIKE '%' || :query || '%'
           OR state LIKE '%' || :query || '%'
        ORDER BY modalPrice DESC
    """)
    fun searchCommodities(query: String): Flow<List<CommodityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(commodities: List<CommodityEntity>)

    @Query("DELETE FROM commodities")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(commodities: List<CommodityEntity>) {
        deleteAll()
        insertAll(commodities)
    }
}
