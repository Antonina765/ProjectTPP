package com.example.myrayon.model

data class Request(
    val id: Int,
    val userId: Int,
    val district: String,
    val text: String,
    val status: String // "New", "In process", "Done"
)