package com.example.authorizationapp

import com.example.authorizationapp.data.AuthNetworking
import com.example.authorizationapp.data.DemoServer
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class AuthorizationUnitTest {

    @Test
    fun actualVersionCheck() = runBlocking {
        // sign in with correct email and correct password, and get token
        val token = AuthNetworking.signIn("yana.l@yahoo.com", "abcdefg12")
        // get actual version
        val version = AuthNetworking.getActualAppVersion(token)
        // update version, and get the new version, the new version need to be greater than the previous
        DemoServer.updateVersion()
        val newVersion = AuthNetworking.getActualAppVersion(token)
        Assert.assertTrue(newVersion > version)
    }

    @Test
    fun wrongCredentialsTest() = runBlocking {
        // signing in with wrong email and wrong password returns DemoServer.ERROR_CREDENTIALS
        val token = AuthNetworking.signIn("a@b.com", "acegikmo")
        Assert.assertEquals(token, DemoServer.ERROR_CREDENTIALS)
    }

    @Test
    fun correctCredentialsTest() = runBlocking {
        // signing in with correct credentials shouldn't returns DemoServer.ERROR_CREDENTIALS,
        // returns token or, in case of network error, DemoResponseCode.message
        val token = AuthNetworking.signIn("yana.l@yahoo.com", "abcdefg12")
        Assert.assertNotEquals(token, DemoServer.ERROR_CREDENTIALS)
    }
}