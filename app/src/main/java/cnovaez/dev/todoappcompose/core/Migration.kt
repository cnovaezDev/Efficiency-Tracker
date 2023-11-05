package cnovaez.dev.todoappcompose.core

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 ** Created by Carlos A. Novaez Guerrero on 11/5/2023 1:22 PM
 ** cnovaez.dev@outlook.com
 **/
val migracion1a2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE tasks ADD COLUMN important INTEGER NOT NULL DEFAULT 0")
    }
}
val migracion2a3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `logins` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, pass INTEGER NOT NULL, secret INTEGER NOT NULL DEFAULT 0)")
    }
}