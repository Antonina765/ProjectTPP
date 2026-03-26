package com.example.myrayon.model

data class Vote(
    val id: Int,
    val question: String,
    val yes: Int,
    val no: Int,
    val abstain: Int
)