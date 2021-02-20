package com.example.authorizationapp.view

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.authorizationapp.R
import com.example.authorizationapp.data.DemoServer
import com.example.authorizationapp.databinding.FragmentAuthorizationBinding


class AuthorizationFragment : Fragment() {

    private var binding: FragmentAuthorizationBinding? = null

    // region lifecycle functions override
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.appName)
        setEmailPasswordTextChangedListener()
        setSignInOnClickButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    // endregion

    private val emailValid: Boolean
        get() = binding?.emailEditText?.length() ?: 0 >= 6 && Patterns.EMAIL_ADDRESS.matcher(binding?.emailEditText?.text.toString()).matches()
    private val passwordValid: Boolean
        get() = binding?.passwordEditText?.length() ?: 0 >= 8

    private fun setEmailPasswordTextChangedListener() {
        binding?.emailEditText?.doOnTextChanged { _, _, _, _ ->
            binding?.signInButton?.isEnabled = emailValid && passwordValid
        }
        binding?.passwordEditText?.doOnTextChanged { _, _, _, _ ->
            binding?.signInButton?.isEnabled = emailValid && passwordValid
        }
    }

    private fun setSignInOnClickButton() = binding?.signInButton?.setOnClickListener {
        it.isEnabled = false // to prevent multi-submission
        val email = binding?.emailEditText?.text.toString()
        val password = binding?.passwordEditText?.text.toString()
        binding?.progressBar?.visibility = View.VISIBLE
        (activity as? AuthorizationActivity)?.viewModel?.signIn(email, password)?.observe(viewLifecycleOwner, { token ->
            when (token) {
                DemoServer.ERROR_CREDENTIALS, DemoServer.ERROR -> AlertDialogFragment().apply {
                    val args = Bundle()
                    args.putString(AlertDialogFragment.MESSAGE_KEY, token)
                    arguments = args
                }.show(childFragmentManager, null)

                else -> if (!token.isNullOrEmpty()) {
                    val action = AuthorizationFragmentDirections.actionAuthorizationFragmentToMainFragment()
                    findNavController().navigate(action)
                }
            }
            binding?.progressBar?.visibility = View.GONE
            it.isEnabled = true
        })
    }
}