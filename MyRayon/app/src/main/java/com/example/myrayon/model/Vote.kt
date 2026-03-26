package com.example.myrayon.model

data class Vote(
    val id: Int,
    val question: String,
    val agree: Int,
    val disagree: Int,
    val abstain: Int
)