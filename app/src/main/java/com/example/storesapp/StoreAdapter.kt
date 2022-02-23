package com.example.storesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.storesapp.databinding.ItemStoreBinding

class StoreAdapter(
    private var stores: MutableList<Store>,
    private var listener: OnClickListener) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemStoreBinding.bind(view)

        fun setListener(store: Store){
            binding.root.setOnClickListener { listener.onClick(store) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_store,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = stores.get(position)
        with(holder){
            setListener(store)
            binding.tvName.text = store.name
        }
    }

    override fun getItemCount(): Int = stores.size

    fun add(store: Store) {
        stores.add(store)
        notifyDataSetChanged()
    }

}
