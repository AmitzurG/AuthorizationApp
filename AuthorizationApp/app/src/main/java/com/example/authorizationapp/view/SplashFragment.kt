package com.example.authorizationapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.authorizationapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = null
        checkAuthorization()
    }

    private fun checkAuthorization() = lifecycleScope.launch {
        delay(2000)
        (activity as AuthorizationActivity).viewModel.authorized.collect {
            val action = if (it) {
                SplashFragmentDirections.actionSplashFragmentToMainFragment()
            } else {
                SplashFragmentDirections.actionSplashFragmentToAutorizationFragment()
            }
            findNavController().navigate(action)
        }
    }
}