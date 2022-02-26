package com.example.storesapp

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.storesapp.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id),0)
        if(id != null && id !=0L){
            Toast.makeText(view.context, id.toString(), Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(view.context, id.toString(), Toast.LENGTH_SHORT).show()
        }

        mActivity = activity as MainActivity

        with(mActivity?.supportActionBar){
            this?.setDisplayHomeAsUpEnabled(true)
            this?.title = getString(R.string.edit_store_title_add)
        }

        setHasOptionsMenu(true)

        mBinding.etPhotUrl.addTextChangedListener {

            Glide.with(this)
                .load(mBinding.etPhotUrl.text.toString())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBinding.ivPhoto)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {

                val store = StoreEntity(
                                name = mBinding.etName.text.toString().trim(),
                                phone = mBinding.etPhone.text.toString().trim(),
                                website = mBinding.etUrl.text.toString().trim(),
                                photoUrl = mBinding.etPhotUrl.text.toString().trim())
                doAsync {
                    store.id = StoreApplication.database.storeDao().addStore(store)

                    uiThread {

                        mActivity?.addStore(store)

                        hideKeyboard()

                        Snackbar.make(mBinding.root,
                            getString(R.string.edit_store_save_message_success),
                            Snackbar.LENGTH_SHORT)
                            .show()

                        //Envia al Main Activity
                        mActivity?.onBackPressed()
                    }
                }
                true
            }else -> return super.onOptionsItemSelected(item)
        }
    }

    fun hideKeyboard(){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(view != null){
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {

        with(mActivity?.supportActionBar){
            this?.setDisplayHomeAsUpEnabled(false)
            this?.title = getString(R.string.app_name)
        }

        setHasOptionsMenu(false)
        super.onDestroy()
    }

}