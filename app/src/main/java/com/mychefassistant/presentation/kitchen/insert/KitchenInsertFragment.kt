package com.mychefassistant.presentation.kitchen.insert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mychefassistant.R
import com.mychefassistant.utils.iconpicker.IconPicker
import org.koin.androidx.viewmodel.ext.android.viewModel

class KitchenInsertFragment : Fragment() {
    private val viewModel: KitchenInsertViewModel by viewModel()
    private lateinit var titleInput: TextInputEditText
    private lateinit var titleInputLayout: TextInputLayout
    private lateinit var locationInput: TextInputEditText
    private lateinit var imageButton: ImageButton
    private lateinit var iconPicker: IconPicker
    private var iconId: Int = R.drawable.ic_baseline_kitchen_24

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_kitchen_insert, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleInput = view.findViewById(R.id.fragment_kitchen_insert_title_layout_input)
        titleInputLayout = view.findViewById(R.id.fragment_kitchen_insert_title_layout)
        locationInput = view.findViewById(R.id.fragment_kitchen_insert_location_layout_input)
        imageButton = view.findViewById(R.id.fragment_kitchen_insert_icon)
        iconPicker = IconPicker(childFragmentManager).setOnClickListener {
            iconId = it.icon
            imageButton.setImageResource(iconId)
        }

        view.findViewById<Button>(R.id.fragment_kitchen_insert_submit).setOnClickListener {
            viewModel.addKitchenRequest(
                title = titleInput.text.toString(),
                location = if (locationInput.text.isNullOrBlank()) null else locationInput.text.toString()
                    .toInt(),
                icon = 1
            )
        }

        imageButton.setOnClickListener {
            iconPicker.show()
        }

        viewModel.eventListener(viewLifecycleOwner)
            .onInfo {
                when (it.type) {
                    KitchenInsertViewModel.backFragment -> activity?.onBackPressed()
                }
            }
            .onError {
                when (it.type) {
                    KitchenInsertViewModel.setTitleInputError ->
                        titleInputLayout.error = it.exception.message
                    KitchenInsertViewModel.createAlertWithButton ->
                        snackBarWithAction(it.data as KitchenInsertViewModel.AlertButtonModel)
                }
            }
    }

    override fun onPause() {
        super.onPause()
        viewModel.resetEvents()
    }

    private fun snackBarWithAction(alert: KitchenInsertViewModel.AlertButtonModel) =
        Snackbar.make(requireView(), alert.title, Snackbar.LENGTH_LONG)
            .setAction(alert.btnTitle) {
                when (alert.action) {
                    KitchenInsertViewModel.routeToGrocery -> routeToGrocery(alert.data as Int)
                }
            }
            .show()

    private fun routeToGrocery(id: Int) = findNavController().navigate(
        R.id.action_fragment_kitchen_insert_to_fragment_grocery_manage, bundleOf("id" to id)
    )
}