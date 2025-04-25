package com.example.wearme.data.network.api

// Все активности, использующие GetBioCallback, должны реализовать этот интерфейс.
interface BioCallbackHandler {
    fun navigateToMain()
    fun navigateToBio()
}