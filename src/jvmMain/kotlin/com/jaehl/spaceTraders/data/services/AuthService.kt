package com.jaehl.spaceTraders.data.services

import javax.inject.Inject

class AuthService @Inject constructor(

) {
    private val token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZGVudGlmaWVyIjoiVEFOR080MiIsImlhdCI6MTY4MzU3MzY4Nywic3ViIjoiYWdlbnQtdG9rZW4ifQ.i-s8G-BriqzJ0SL2LHjIW39YLTt_nR36wDwes8TuTJpga17e4ONuOh6YqQdiWzID_vTlomQksZYot7IyeH0AZL3415Wd0V8dbxp3oYCYETRepZrqtkPGHWw14brGORuEvhLt281Rawr214mC93kYbwqvjf6SmwCweWqeckgz_wzdxoHR1zqaShcf6gO3sZFkB9PMnZ6Ceo3Oa8v7-UTmzY0tfHR1FR9379gY5DJO6yPwyecAyQqc0Nwbhby0bMNO_GCrqfvkTs7-Pmn7vhHeyeI9bFzW0QCctUybqv_HjwO3fEkDRDU35qi7vHyem-tFmwSS6IIKp4l6wLPee6HBawkvLvUa2syWHH9qForj1fmDzcYbt7Ttbx0jCw7Oly27-T5NGEO-0bSpFQDE4hzdE2SvNT5R3VIQN1Cqxbl3MLON_zflPvvobqLdClANrOSKE4DJl8_NR4mVF-xTYvd7qcQ483-YYMEQhaYKLfQfelPgfRrSF3c-hLH8jW_91eDa"

    fun getBearerToken() : String {
        return "$bearerToken $token"
    }

    companion object {
        private const val bearerToken = "Bearer"
    }
}