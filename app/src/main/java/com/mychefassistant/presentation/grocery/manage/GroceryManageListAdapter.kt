package com.mychefassistant.presentation.grocery.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.mychefassistant.core.domain.Grocery
import com.mychefassistant.databinding.FragmentGroceryManageListItemBinding
import com.mychefassistant.utils.Event

class GroceryManageListAdapter(private val eventGate: (Event.Info) -> Unit) :
    ListAdapter<Grocery, GroceryManageListViewHolder>(GroceryManageListDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryManageListViewHolder =
        GroceryManageListViewHolder(
            FragmentGroceryManageListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: GroceryManageListViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.grocery = item

        holder.itemView.setOnClickListener {
            eventGate(Event.Info(GroceryManageViewModel.groceryUpdateRequest, item))
        }
    }
}