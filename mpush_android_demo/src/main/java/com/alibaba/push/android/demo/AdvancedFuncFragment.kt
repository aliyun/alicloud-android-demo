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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.push.android.demo.databinding.AdvancedFuncFragmentBinding
import com.alibaba.push.android.demo.databinding.CountLimitDialogBinding
import com.alibaba.sdk.android.push.CloudPushService
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * 高级功能Fragment
 * @author ren
 * @date 2024/09/20
 */
class AdvancedFuncFragment : Fragment() {

    private lateinit var binding: AdvancedFuncFragmentBinding

    private lateinit var viewModel: AdvanceFuncViewModel

    private var deviceTagAdapter: AllLabelAdapter? = null
    private var aliasTagAdapter: AllLabelAdapter? = null
    private var accountTagAdapter: AllLabelAdapter? = null

    private val requestDataLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val type = result.data?.getIntExtra("type", 0) ?: 0
                when (type) {
                    DataSource.LABEL_DEVICE_TAG -> {}
                    DataSource.LABEL_ALIAS -> {}
                    DataSource.LABEL_ALIAS_TAG -> {}
                    DataSource.LABEL_ACCOUNT_TAG -> {}
                }
            }
        }

    private val addTagLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.apply {
                    val tag = getStringExtra(KEY_TAG) ?: ""
                    val target =
                        getIntExtra(KEY_TAG_TARGET_TYPE, CloudPushService.DEVICE_TARGET)
                    var alias: String? = ""
                    if (target == CloudPushService.ALIAS_TARGET) {
                        alias = getStringExtra(KEY_ALIAS)
                    }
                    viewModel.addTag(tag, target, alias)
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
        binding.tvAlreadyAddTag.setOnClickListener { addTag() }

        viewModel.deviceTagData.observe(viewLifecycleOwner) {
            deviceTagAdapter?.labels?.clear()
            deviceTagAdapter?.labels?.addAll(
                if (true == viewModel.showMoreDeviceTag.value) viewModel.deviceTags.subList(
                    0,
                    viewModel.showMoreTagCount
                ) else viewModel.deviceTags
            )
            deviceTagAdapter?.notifyDataSetChanged()
        }

        viewModel.aliasTagData.observe(viewLifecycleOwner) {
            aliasTagAdapter?.labels?.clear()
            aliasTagAdapter?.labels?.addAll(if (true == viewModel.showMoreAliasTag.value) viewModel.aliasTags.subList(
                0,
                viewModel.showMoreTagCount
            ) else viewModel.aliasTags)
            aliasTagAdapter?.notifyDataSetChanged()
        }

        viewModel.accountTagData.observe(viewLifecycleOwner) {
            accountTagAdapter?.labels?.clear()
            accountTagAdapter?.labels?.addAll(if (true == viewModel.showMoreAccountTag.value) viewModel.accountTags.subList(
                0,
                viewModel.showMoreTagCount
            ) else viewModel.accountTags)
            accountTagAdapter?.notifyDataSetChanged()
        }

        binding.tvMoreDeviceTag.setOnClickListener { lookAllTag(LABEL_DEVICE_TAG) }
        binding.tvMoreAliasTag.setOnClickListener { lookAllTag(LABEL_ALIAS_TAG) }
        binding.tvMoreAccountTag.setOnClickListener { lookAllTag(LABEL_ACCOUNT_TAG) }
        binding.tvAliasMore.setOnClickListener { lookAllTag(LABEL_ALIAS) }

    }


    private fun initAlias() {
        viewModel.aliasListStr.observe(viewLifecycleOwner) {
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

    private fun addAlias() {
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

    private fun updateAlias() {
        binding.rvLabelAlias.setData(viewModel.currAliasList)
    }

    private fun initTag() {
        binding.rvDeviceTag.apply {
            layoutManager =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            addItemDecoration(GridSpacingItemDecoration(3, 8.toDp()))
            deviceTagAdapter = AllLabelAdapter(mutableListOf()).apply {
                deleteLabelCallback = {
                    viewModel.removeDeviceTag(it)
                }
            }
            adapter = deviceTagAdapter
        }

        binding.rvAliasTag.apply {
            layoutManager =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            addItemDecoration(GridSpacingItemDecoration(3, 8.toDp()))
            aliasTagAdapter = AllLabelAdapter(mutableListOf()).apply {
                deleteLabelCallback = {
                    viewModel.removeAliasTag(it)
                }
            }
            adapter = aliasTagAdapter
        }

        binding.rvAccountTag.apply {
            layoutManager =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            addItemDecoration(GridSpacingItemDecoration(3, 8.toDp()))
            accountTagAdapter = AllLabelAdapter(mutableListOf()).apply {
                deleteLabelCallback = {
                    viewModel.removeAccountTag(it)
                }
            }
            adapter = accountTagAdapter
        }
    }

    private fun addTag() {
        val intent = Intent(requireContext(), AddTagActivity::class.java)
        addTagLauncher.launch(intent)
    }

    private fun lookAllTag(type: Int) {
        val intent = Intent(requireContext(), AllLabelActivity::class.java).apply {
            putExtra("type", type)
        }
        requestDataLauncher.launch(intent)
    }

    private fun showCountLimitDialog() {
        val countLimitDialogBinding =
            CountLimitDialogBinding.inflate(LayoutInflater.from(requireContext()))
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
        requireContext().showInputDialog(
            R.string.push_text_notification, R.string.push_text_notification_hint,
            showAlert = false,
            showAliasInput = false
        ) { it, _ ->
            viewModel.bindPhone(requireContext(), it)
        }
    }
}