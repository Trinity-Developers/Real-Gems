package com.trinitydevelopers.realgemsadmin.pojos

import java.io.Serializable

data class ListItem(
    val value: String?="",
    val description: String?=""
):Serializable {
    // No-argument constructor for Firestore
    constructor() : this("", "")
}
