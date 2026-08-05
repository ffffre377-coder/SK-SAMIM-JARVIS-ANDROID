package com.samim.jarvis.automation

data class Reminder(
    val id: String,
    val title: String,
    val body: String,
    val timeEpochMs: Long
)
