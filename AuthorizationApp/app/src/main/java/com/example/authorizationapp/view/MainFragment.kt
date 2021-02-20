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
import com.example.authorizationapp.databinding.FragmentMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    private var binding: FragmentMainBinding? = null

    // region lifecycle functions override
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.appName)
        checkVersion()
        setLastAuthorization()
        setSignOutClickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    // endregion

    // region private functions
    private fun checkVersion() = lifecycleScope.launch {
        binding?.progressBar?.visibility = View.VISIBLE
        (activity as AuthorizationActivity).viewModel.currentVersion.collect { currentVersion ->
            (activity as AuthorizationActivity).viewModel.getActualAppVersion().observe(viewLifecycleOwner, { actualVersion ->
                if (currentVersion ?: -1 < actualVersion) {
                    // if the current app version code is less than the actual version, show a dialog with "Your app is outdated" title
                    // and "Close" button (should close the app)
                    AlertDialogFragment().apply {
                        val args = Bundle()
                        args.putString(AlertDialogFragment.TITLE_KEY, this@MainFragment.context?.getString(R.string.appOutdated))
                        args.putString(AlertDialogFragment.MESSAGE_KEY, "")
                        args.putString(AlertDialogFragment.BUTTON_KEY, this@MainFragment.context?.getString(R.string.close))
                        arguments = args
                        onClick = {
                            activity?.finish() // clicking on "Close" button closes the app
                        }
                    }.show(childFragmentManager, null)
                }
                binding?.progressBar?.visibility = View.GONE
            })
        }
    }

    private fun setLastAuthorization() = lifecycleScope.launch {
        (activity as AuthorizationActivity).viewModel.lastAuthorizationDate.collect {
            binding?.lastAuthorizationTextView?.text = it
        }
    }

    private fun setSignOutClickListener() = binding?.signOutButton?.setOnClickListener {
        (activity as AuthorizationActivity).viewModel.signOut()
        val action = MainFragmentDirections.actionMainFragmentToAuthorizationFragment()
        findNavController().navigate(action)
    }
    // endregion
}