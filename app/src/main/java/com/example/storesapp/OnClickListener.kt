package com.example.storesapp

interface OnClickListener {

    fun onClick(storeId: Long)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDeleteStore(storeEntity: StoreEntity)
}