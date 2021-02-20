package com.example.authorizationapp.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.authorizationapp.R
import com.example.authorizationapp.viewmodel.AuthorizationViewModel

class AuthorizationActivity : AppCompatActivity() {

    val viewModel: AuthorizationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        setSupportActionBar(findViewById(R.id.toolbar))
    }
}