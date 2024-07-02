package com.trinitydevelopers.realgemsadmin.pojos

data class Gems(
    val nameId: String?="",
    val cutId: String?="",
    val origin: String?="",
    val shapeId: String?="",
    val compositionId: String?="",
    val treatmentId: String?="",
    val color: String?="",
    val carats: Double?=0.0,
    val imageUrls: List<String> = emptyList() // Optional, can be set later
)
