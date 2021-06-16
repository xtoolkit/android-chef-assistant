package com.mychefassistant.presentation.grocery.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.mychefassistant.core.domain.Grocery
import com.mychefassistant.databinding.FragmentGroceryManageBinding
import com.mychefassistant.presentation.grocery.insert.GroceryInsertFragment
import com.mychefassistant.presentation.main.MainActivity
import com.mychefassistant.presentation.main.alert.MainAlertModel
import com.mychefassistant.presentation.main.modal.MainModalModel
import com.mychefassistant.utils.Event
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroceryManageFragment : Fragment() {
    private val viewModel: GroceryManageViewModel by viewModel()
    private val args: GroceryManageFragmentArgs by navArgs()
    private val kitchenId by lazy { args.kitchenId }
    private var binding: FragmentGroceryManageBinding? = null
    private var modal: GroceryInsertFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroceryManageBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity

        activity.viewModel.setFabOnClickListener {
            viewModel.setViewEvent(Event.Info(GroceryManageViewModel.requestShowInsertModal))
        }

        viewModel
            .onInfo {
                when (it.type) {
                    GroceryManageViewModel.onKitchenLoad -> binding?.kitchen = viewModel.kitchen
                    GroceryManageViewModel.onGroceriesLoad -> setupListView()
                    GroceryManageViewModel.showInsertModal -> showInsertModal(it.data as? Grocery)
                    GroceryManageViewModel.closeInsertModal -> modal?.dismiss()
                    GroceryManageViewModel.modalEvent -> modal?.onParentEventListener(it.data as Event)
                    GroceryManageViewModel.createModal -> activity.viewModel.setModal(it.data as MainModalModel)
                    GroceryManageViewModel.createAlert -> activity.viewModel.setAlert(it.data as MainAlertModel)
                }
            }
            .onError {
                when (it.type) {
                    GroceryManageViewModel.createErrorAlert -> it.exception.message?.let { x ->
                        activity.viewModel.setAlert(MainAlertModel(x))
                    }
                }
            }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted { viewModel.eventListener() }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted { viewModel.start(kitchenId) }
    }

    override fun onPause() {
        viewLifecycleOwner.lifecycleScope.launch { viewModel.resetEvents() }
        super.onPause()
    }

    private fun setupListView() {
        val adapter = GroceryManageListAdapter { viewModel.setViewEvent(it) }
        binding!!.fragmentGroceryManageList.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.groceries?.collect { adapter.submitList(it) }
        }
    }

    private fun showInsertModal(grocery: Grocery?) {
        modal = GroceryInsertFragment(childFragmentManager, grocery) { viewModel.setViewEvent(it) }
        modal?.show()
    }
}