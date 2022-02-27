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
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private var mStoreEntity: StoreEntity? = null

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
            mIsEditMode = true
            getStore(id)
        }else{
            mStoreEntity = StoreEntity(name = "",phone = "",website = "",photoUrl = "")
        }

        mActivity = activity as MainActivity

        setupActionBar()
        setupTextFields()
    }

    private fun setupActionBar() {
        with(mActivity?.supportActionBar){
            this?.setDisplayHomeAsUpEnabled(true)
            this?.title = if(mIsEditMode) getString(R.string.edit_store_title_edit)
                          else getString(R.string.edit_store_title_add)
        }

        setHasOptionsMenu(true)
    }

    private fun setupTextFields(){
        with((mBinding)){
            etName.addTextChangedListener { validateFields(tilName) }
            etPhone.addTextChangedListener { validateFields(tilPhone) }
            etPhotoUrl.addTextChangedListener {
                validateFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    private fun loadImage(url:String){
        Glide.with(this)
            .load(url)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding.ivPhoto)
    }

    private fun getStore(id: Long) {
        doAsync {
            mStoreEntity = StoreApplication.database.storeDao().getStore(id)
            uiThread { if(mStoreEntity != null) setUIStore(mStoreEntity!!)  }
        }
    }

    private fun setUIStore(storeEntity: StoreEntity) {
        with(mBinding){
            etName.setText(storeEntity.name)
            etPhone.setText(storeEntity.phone)
            etUrl.setText(storeEntity.website)
            etPhotoUrl.setText(storeEntity.photoUrl)
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
                if(mStoreEntity != null && validateFields(mBinding.tilPhotoUrl, mBinding.tilPhone, mBinding.tilName)){
                    with(mStoreEntity!!){
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etUrl.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    doAsync {

                        if(mIsEditMode) StoreApplication.database.storeDao().updateStore(mStoreEntity!!)
                        else mStoreEntity!!.id = StoreApplication.database.storeDao().addStore(mStoreEntity!!)

                        uiThread {

                            hideKeyboard()

                            if(mIsEditMode){
                                mActivity?.updateStore(mStoreEntity!!)

                                Snackbar.make(mBinding.root,
                                    getString(R.string.edit_store_update_message_success),
                                    Snackbar.LENGTH_SHORT)
                                    .show()
                            } else {
                                mActivity?.addStore(mStoreEntity!!)

                                Toast.makeText(mBinding.root.context,
                                    getString(R.string.edit_store_save_message_success),
                                    Toast.LENGTH_SHORT)
                                    .show()

                                //Envia al Main Activity
                                mActivity?.onBackPressed()
                            }
                        }
                    }
                }
                true
            }else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun validateFields(vararg textFields: TextInputLayout): Boolean {
        var isValid = true

        for (textfield in textFields){
            if(textfield.editText?.text.toString().trim().isEmpty()){
                textfield.error = getString(R.string.helper_required)
                isValid = false
            } else textfield.error = null
        }

        if(!isValid) Snackbar.make(mBinding.root,
            getString(R.string.edit_store_message_valid),
            Snackbar.LENGTH_SHORT).show()

        return isValid
    }

    /***This one is a way to validate inputs each other**/
    private fun validateFields(): Boolean {
        var isValid = true

        if(mBinding.etPhotoUrl.text.toString().trim().isEmpty()){
            mBinding.tilPhotoUrl.error = getString(R.string.helper_required)
            mBinding.etPhotoUrl.requestFocus()
            isValid = false
        }

        if(mBinding.etPhone.text.toString().trim().isEmpty()){
            mBinding.tilPhone.error = getString(R.string.helper_required)
            mBinding.etPhone.requestFocus()
            isValid = false
        }

        if(mBinding.etName.text.toString().trim().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.etName.requestFocus()
            isValid = false
        }

        return isValid
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