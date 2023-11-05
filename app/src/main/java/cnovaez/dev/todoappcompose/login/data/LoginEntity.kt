package cnovaez.dev.todoappcompose.login.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 ** Created by Carlos A. Novaez Guerrero on 11/5/2023 3:30 PM
 ** cnovaez.dev@outlook.com
 **/
@Entity(tableName = "logins")
data class LoginEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "pass")
    val pass: Int = -1,
    @ColumnInfo(name = "secret")
    val secret: Boolean,

)