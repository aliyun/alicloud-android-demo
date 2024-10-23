package com.alibaba.push.android.demo

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alibaba.push.android.demo.databinding.AdvancedFuncFragmentBinding
import com.alibaba.push.android.demo.databinding.CountLimitDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * 高级功能Fragment
 * @author ren
 * @date 2024/09/20
 */
class AdvancedFuncFragment : Fragment() {

    private lateinit var binding: AdvancedFuncFragmentBinding

    private lateinit var viewModel: AdvanceFuncViewModel

    private val requestDataLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val type = result.data?.getIntExtra("type",0)?:0
                when(type) {
                    DataSource.LABEL_DEVICE_TAG -> {}
                    DataSource.LABEL_ALIAS -> {}
                    DataSource.LABEL_ALIAS_TAG -> {}
                    DataSource.LABEL_ACCOUNT_TAG -> {}
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AdvanceFuncViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AdvancedFuncFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTag()
        initAlias()
        binding.clPhone.setOnClickListener { showPhoneInputDialog() }
        binding.clAccount.setOnClickListener { showAccountInputDialog() }
        val statusBarHeight = requireContext().getStatusBarHeight()
        binding.vStatusBg.layoutParams?.height = statusBarHeight
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val alpha = if (scrollY * 1f / statusBarHeight > 1) {
                1f
            } else {
                scrollY * 1f / statusBarHeight
            }
            binding.vStatusBg.alpha = alpha
        }
        binding.tvCountLimit.setOnClickListener {
            showCountLimitDialog()
        }

        viewModel.initData()

        binding.tvAddTagEmpty.setOnClickListener { addTag() }

    }


    private fun initAlias(){
        viewModel.aliasListStr.observe(viewLifecycleOwner){
            updateAlias()
        }
        binding.rvLabelAlias.apply {
            addLabelClickCallback = {
                addAlias()
            }
            deleteLabelClickCallback = {
                deleteAlias(it)
            }
        }
    }

    private fun addAlias(){
        requireContext().showInputDialog(
            R.string.push_add_alias, R.string.push_input_alias_hint,
            showAlert = false,
            showAliasInput = false
        ) { it, _ ->
            if (viewModel.alreadyAddAlias(it)) {
                requireContext().toast(R.string.push_already_add)
                return@showInputDialog
            }
            viewModel.addAlias(requireContext(), it) {
                updateAlias()
            }
        }
    }

    private fun deleteAlias(alias: String) {
        viewModel.removeAlias(requireContext(), alias) {
            updateAlias()
        }
    }

    private fun updateAlias(){
        binding.rvLabelAlias.setData(viewModel.currAliasList)
    }

    private fun initTag(){
//        binding.rv.apply {
//            addLabelClickCallback = {
//                addTag()
//            }
//            deleteLabelClickCallback = {
//                deleteTag(it)
//            }
//        }

    }

    private fun addTag(){
        val intent = Intent(requireContext(), AddTagActivity::class.java)
        requestDataLauncher.launch(intent)
    }

    private fun deleteTag(tag: String){

    }

    private fun lookAllTag(type:Int) {
        val intent = Intent(requireContext(), AllLabelActivity::class.java).apply {
            putExtra("type" , type)
        }
        requestDataLauncher.launch(intent)
    }

    private fun showCountLimitDialog(){
        val countLimitDialogBinding = CountLimitDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = BottomSheetDialog(requireContext(), R.style.RoundedBottomSheetDialog).apply {
            setContentView(countLimitDialogBinding.root)
            countLimitDialogBinding.lifecycleOwner = this
            show()
        }
        countLimitDialogBinding.ivClose.setOnClickListener { dialog.dismiss() }
    }

    private fun showAccountInputDialog() {
        requireContext().showInputDialog(
            R.string.push_bind_account, R.string.push_bind_account_hint,
            showAlert = false,
            showAliasInput = false
        ) { it, _ ->
            viewModel.bindAccount(requireContext(), it)
        }
    }

    private fun showPhoneInputDialog() {
        requireContext().showInputDialog(R.string.push_text_notification, R.string.push_text_notification_hint,
            showAlert = false,
            showAliasInput = false) {it, _ ->
            viewModel.bindPhone(requireContext(), it)
        }
    }
}