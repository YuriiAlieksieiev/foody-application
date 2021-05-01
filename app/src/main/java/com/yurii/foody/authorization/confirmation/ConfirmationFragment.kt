package com.yurii.foody.authorization.confirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yurii.foody.R
import com.yurii.foody.api.UserRoleEnum
import com.yurii.foody.databinding.FragmentConfirmationBinding
import com.yurii.foody.utils.Injector
import com.yurii.foody.utils.observeOnLifecycle

class ConfirmationFragment : Fragment() {
    private val viewModel: ConfirmationViewModel by viewModels {
        Injector.provideConfirmationViewModel(
            requireContext(),
            args.userIsNotConfirmed,
            args.role,
        )
    }
    private val args: ConfirmationFragmentArgs by navArgs()
    private lateinit var binding: FragmentConfirmationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_confirmation, container, false)
        binding.viewModel = viewModel
        observeEvents()
        return binding.root
    }

    private fun observeEvents() {
        viewModel.eventFlow.observeOnLifecycle(viewLifecycleOwner) {
            when (it) {
                is ConfirmationViewModel.Event.NavigateToAuthorizationFragment -> navigateToAuthorizationFragment()
                is ConfirmationViewModel.Event.NavigateToChoosingRoleFragment -> navigateToChoosingRoleFragment(it.roleEnum)
                is ConfirmationViewModel.Event.ShowRoleIsNotConfirmed -> {
                    binding.hint.setText(R.string.hint_confirmation_executor_role)
                    binding.changeRole.isVisible = true
                }
                ConfirmationViewModel.Event.ShowUserIsNotConfirmed -> {
                    binding.hint.setText(R.string.hint_confirmation_email)
                    binding.changeRole.isVisible = false
                }
            }
        }
    }

    private fun navigateToAuthorizationFragment() {
        findNavController().navigate(ConfirmationFragmentDirections.actionConfirmationFragmentToAuthenticationFragment())
    }

    private fun navigateToChoosingRoleFragment(role: UserRoleEnum) {
        findNavController().navigate(ConfirmationFragmentDirections.actionConfirmationFragmentToChooseRoleFragment(role, selectNewRole = true))
    }
}