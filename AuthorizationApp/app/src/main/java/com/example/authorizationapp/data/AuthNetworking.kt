package com.example.authorizationapp.data

import com.google.gson.JsonParser

object AuthNetworking {

    suspend fun getActualAppVersion(token: String): Long {
        val connection = DemoServer.DemoHttpUrlConnection()
        val response = request(
                connection,
                "version",
                headerParams = arrayOf(Pair("Authorization", "Bearer $token")) // token should be used in the header for further requests after sign in
        )
        return if (connection.responseCode != DemoServer.DemoResponseCode.HTTP_OK) -1 else {
            val jsonResponse = JsonParser.parseString(response).asJsonObject
            jsonResponse["version"].asLong
        }
    }

    suspend fun signIn(email: String, password: String): String {
        val connection = DemoServer.DemoHttpUrlConnection()
        val response = request(
                connection,
                "signIn",
                params = arrayOf(Pair("email", email), Pair("password", password))
        )
        return if (connection.responseCode != DemoServer.DemoResponseCode.HTTP_OK) connection.responseCode.message else {
            val jsonResponse = JsonParser.parseString(response).asJsonObject
            jsonResponse["token"].asString
        }
    }

    private suspend fun request(
            connection: DemoServer.DemoHttpUrlConnection,
            request: String, headerParams: Array<Pair<String, String>> = emptyArray(), vararg params: Pair<String, String> = emptyArray()): String {
        connection.request = request
        for (param in headerParams) {
            connection.setRequestProperty(param.first /*key*/, param.second /*value*/)
        }
        for (param in params) {
            connection.addRequestParams(param.first /*key*/, param.second /*value*/)
        }
        var response = connection.streamRead()
        if (connection.responseCode != DemoServer.DemoResponseCode.HTTP_OK) { // if error, try to fetch the request once again
            response = connection.streamRead()
        }
        return if (connection.responseCode != DemoServer.DemoResponseCode.HTTP_OK) {
            connection.responseCode.message
        } else {
            response
        }
    }
}
