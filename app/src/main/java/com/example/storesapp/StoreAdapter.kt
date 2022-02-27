package com.example.storesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.storesapp.databinding.ItemStoreBinding

class StoreAdapter(
    private var stores: MutableList<StoreEntity>,
    private var listener: OnClickListener) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemStoreBinding.bind(view)

        fun setListener(storeEntity: StoreEntity){

            with(binding.root){
                setOnClickListener { listener.onClick(storeEntity.id) }
                setOnLongClickListener {
                    listener.onShowActions(storeEntity)
                    true
                }
            }

            binding.chFavorite.setOnClickListener { listener.onFavoriteStore(storeEntity) }
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
            binding.chFavorite.isChecked = store.isFavorite

            Glide.with(binding.root.context)
                .load(store.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.imgPhoto)
        }
    }

    override fun getItemCount(): Int = stores.size

    fun add(storeEntity: StoreEntity) {
        if(!stores.contains(storeEntity)){
            stores.add(storeEntity)
            notifyItemInserted(stores.size-1)
        }
    }

    fun setStores(stores: MutableList<StoreEntity>) {
        this.stores = stores
        notifyDataSetChanged()
    }

    fun update(storeEntity: StoreEntity) {
        val index = stores.indexOf(storeEntity)

        if(index != -1){
            stores.set(index, storeEntity)
            notifyItemChanged(index)
        }
    }

    fun delete(storeEntity: StoreEntity) {
        val index = stores.indexOf(storeEntity)
        if(index != -1){
            stores.removeAt(index)
            notifyItemRemoved(index)
        }
    }

}
