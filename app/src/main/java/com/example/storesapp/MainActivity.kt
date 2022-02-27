package com.example.storesapp

import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.storesapp.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.btnSave.setOnClickListener {
            val store = StoreEntity(name = binding.etName.text.trim().toString())
            Thread{
                StoreApplication.database.storeDao().addStore(store)
            }.start()

            mAdapter.add(store)
        }*/

        binding.fab.setOnClickListener { launchEditStoreFragment() }

        setupRecyclerView()
    }

    private fun launchEditStoreFragment(args: Bundle? = null) {

        val fragment = EditStoreFragment()
        if(args != null) fragment.arguments = args

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun setupRecyclerView() {

        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))

        getStores()

        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = mAdapter
            layoutManager = mGridLayout
        }
    }

    private fun getStores(){

        doAsync {
            val stores = StoreApplication.database.storeDao().getAllStores()

            uiThread {
                mAdapter.setStores(stores)
            }
        }
    }


    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)

        launchEditStoreFragment(args)

    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                updateStore(storeEntity)
            }
        }
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {

    }

    override fun onShowActions(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(this)
            .setItems(items, { dialog, which ->
                when(which){
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> goToWebsite(storeEntity.website)
                }
            })
            .show()
    }

    private fun dial(phone: String){
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        startIntent(callIntent)
    }

    private fun goToWebsite(website: String){
        if(website.isEmpty()){
            Toast.makeText(this, getString(R.string.main_error_website_empty), Toast.LENGTH_LONG).show()
        }else{
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            startIntent(websiteIntent)
        }
    }

    private fun startIntent(intent: Intent) {
        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        }else{
            Toast.makeText(this, getString(R.string.erro_no_compatible_apps), Toast.LENGTH_LONG).show()
        }
    }

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(getString(R.string.delete_confirm), { dialog, which ->
                doAsync {
                    StoreApplication.database.storeDao().deleteStore(storeEntity)
                    uiThread {
                        mAdapter.delete(storeEntity)
                    }
                }
            })
            .setNegativeButton(getString(R.string.delete_cancel), null)
            .show()
    }

    /**
     * MainAux
     * */
    override fun fabHide(isVisible: Boolean) {
        if(isVisible) binding.fab.show() else binding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
}