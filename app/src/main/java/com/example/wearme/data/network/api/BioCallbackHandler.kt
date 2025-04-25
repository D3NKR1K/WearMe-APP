package com.example.wearme.data.network.api

// Все активности, использующие CheckBioCallback, должны реализовать этот интерфейс.
interface BioCallbackHandler {
    fun navigateToMain()
    fun navigateToBio()
}