package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ElectionItemBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    var elections: List<Election> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = elections.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        //DONE: Bind ViewHolder
        val binding = ElectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ElectionViewHolder(binding)
    }


    //TODO: Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.election = elections[position]
            it.clickListener = clickListener
        }
    }
    //TODO: Add companion object to inflate ViewHolder (from)
}
//TODO: Create ElectionViewHolder
class ElectionViewHolder(val viewDataBinding: ElectionItemBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.election_item
    }
}


//TODO: Create ElectionDiffCallback
class ElectionDiffCallback() : DiffUtil.ItemCallback<Election>() {

    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }
}

//TODO: Create ElectionListener
class ElectionListener(val block: (Election) -> Unit) {
    fun onClick(election: Election) = block(election)
}


