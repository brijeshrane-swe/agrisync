package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CommodityDao
import com.example.data.local.entity.CommodityEntity

@Database(
    entities = [CommodityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AgriSyncDatabase : RoomDatabase() {

    abstract fun commodityDao(): CommodityDao

    companion object {
        @Volatile
        private var INSTANCE: AgriSyncDatabase? = null

        fun getDatabase(context: Context): AgriSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgriSyncDatabase::class.java,
                    "agrisync_market.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
