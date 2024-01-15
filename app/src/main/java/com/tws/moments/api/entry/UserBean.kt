package com.tws.moments.api.entry

import com.google.gson.annotations.SerializedName

data class UserBean(
    var username: String? = null,
    var nick: String? = null,
    val avatar: String? = null,
    @SerializedName("profile-image")
    val profileImage: String? = null,
)