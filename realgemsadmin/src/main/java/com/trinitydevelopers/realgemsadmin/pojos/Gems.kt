package com.trinitydevelopers.realgemsadmin.pojos

import java.io.Serializable

data class Gems(
    var gemId: String?="",
    var nameId: String?="",
    var cutId: String?="",
    var origin: String?="",
    var shapeId: String?="",
    var compositionId: String?="",
    var treatmentId: String?="",
    var color: String?="",
    var carats: Double?=0.0,
    val imageUrls: MutableList<String> = mutableListOf() ,// Optional, can be set later,
    var pinned: Boolean = false


): Serializable
