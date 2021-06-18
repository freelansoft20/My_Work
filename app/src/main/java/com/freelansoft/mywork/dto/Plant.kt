package com.freelansoft.mywork.dto

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Plant(var genus: String, var species : String, var common :String) {
    override fun toString(): String {
        return common
    }
}