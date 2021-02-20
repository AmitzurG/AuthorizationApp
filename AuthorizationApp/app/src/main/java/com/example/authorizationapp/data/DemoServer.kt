package com.example.authorizationapp.data

import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

object DemoServer {
    const val ERROR = "error"
    const val ERROR_CREDENTIALS = "error credentials"

    private var currentVersion = 1
    private val credentialsTokenMap = hashMapOf(
        Pair("adam@gmail.com", "12345678") to UUID.randomUUID().toString(),
        Pair("yana.l@yahoo.com", "abcdefg12") to UUID.randomUUID().toString(),
        Pair("someone@gmail.com", "AbCd12345") to UUID.randomUUID().toString()
    )

    class DemoHttpUrlConnection {
        private val requestProperty = HashMap<String, String>()
        private val requestParams = HashMap<String, String>()
        var request: String? = null
        var responseCode = DemoResponseCode.HTTP_OK
            private set

        fun setRequestProperty(key: String, value: String) {
            requestProperty[key] = value
        }

        fun addRequestParams(key: String, value: String) {
            requestParams[key] = value
        }

        suspend fun streamRead(): String {
            delay(1000) // demonstrates networking request
            responseCode = responseCode() // 20% network error occur
            return when {
                request == "signIn" &&
                        requestParams.containsKey("email") &&
                        requestParams.containsKey("password") -> {
                    val token = credentialsTokenMap[Pair(requestParams["email"], requestParams["password"])]
                    if (token == null) {
                        responseCode = DemoResponseCode.HTTP_UNAUTHORIZED
                    }
                    if (token == null) "{ token : \"$ERROR\" }" else "{ token : \"$token\" }"
                }

                request == "version" &&
                        requestProperty.containsKey("Authorization") &&
                        (requestProperty["Authorization"]?.startsWith("Bearer") ?: false) -> "{ version : $currentVersion }"

                else -> "{}" // empty json
            }
        }

        private fun responseCode(): DemoResponseCode { // 20% network error occur
            val response = arrayListOf(DemoResponseCode.HTTP_NOT_FOUND, DemoResponseCode.HTTP_SERVER_ERROR)
            repeat(8) {
                response.add(DemoResponseCode.HTTP_OK)
            }
            val index = Random.nextInt(10)
            return response[index]
        }
    }

    enum class DemoResponseCode(private val code: Int, val message: String = "") {
        HTTP_OK(200),
        HTTP_UNAUTHORIZED(401, ERROR_CREDENTIALS),
        HTTP_NOT_FOUND(404),
        HTTP_SERVER_ERROR(500)
    }

    fun updateVersion() = currentVersion++
}