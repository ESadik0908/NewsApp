package com.example.newsapp.models

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class User(val spotlight: String?=null, val topics: String? = null){

}
