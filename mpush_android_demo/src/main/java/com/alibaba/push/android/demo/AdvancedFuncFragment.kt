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

    private var deviceTagAdapter: LabelAdapter? = null
    private var aliasTagAdapter: LabelAdapter? = null
    private var accountTagAdapter: LabelAdapter? = null
    private var aliasAdapter: AliasLabelAdapter? = null

    private val requestDataLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val type = result.data?.getIntExtra(TYPE, 0)
                when (type) {
                    LABEL_DEVICE_TAG -> viewModel.getDeviceTagFromServer()
                    LABEL_ALIAS_TAG -> viewModel.getAliasTagFromSp()
                    LABEL_ACCOUNT_TAG -> viewModel.getAccountTagFromSp()
                    LABEL_ALIAS -> viewModel.getAliasListFromServer()
                }
            }
        }

    private val addTagLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.apply {
                    val tag = getStringExtra(KEY_TAG)
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
        initTag()
        initAlias()
        initObserver()
        viewModel.initData()
        binding.tvCountLimit.setOnClickListener { showCountLimitDialog() }
        binding.tvAddTagEmpty.setOnClickListener { addTag() }
        binding.tvAlreadyAddTag.setOnClickListener { addTag() }
        binding.tvMoreDeviceTag.setOnClickListener { lookAllTag(LABEL_DEVICE_TAG) }
        binding.tvMoreAliasTag.setOnClickListener { lookAllTag(LABEL_ALIAS_TAG) }
        binding.tvMoreAccountTag.setOnClickListener { lookAllTag(LABEL_ACCOUNT_TAG) }
        binding.tvAliasMore.setOnClickListener { lookAllTag(LABEL_ALIAS) }
        binding.clAccount.setOnClickListener { showAccountInputDialog() }
        binding.clPhone.setOnClickListener { showPhoneInputDialog() }
    }

    private fun initTag() {
        binding.rvDeviceTag.apply {
            layoutManager =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            addItemDecoration(GridSpacingItemDecoration(3, 8.toDp()))
            deviceTagAdapter = LabelAdapter(mutableListOf()).apply {
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
            aliasTagAdapter = LabelAdapter(mutableListOf()).apply {
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
            accountTagAdapter = LabelAdapter(mutableListOf()).apply {
                deleteLabelCallback = {
                    viewModel.removeAccountTag(it)
                }
            }
            adapter = accountTagAdapter
        }
    }

    private fun initAlias() {
        binding.rvAlias.apply {
            layoutManager =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            addItemDecoration(GridSpacingItemDecoration(3, 8.toDp()))
            aliasAdapter = AliasLabelAdapter(mutableListOf()).apply {
                deleteLabelCallback = {
                    viewModel.removeAlias(it)
                }
                addLabelCallback = {
                    addAlias()
                }
            }
            adapter = aliasAdapter
        }
    }

    private fun initObserver() {
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

        viewModel.aliasListStr.observe(viewLifecycleOwner) {
            val showAliasData = mutableListOf<String>().apply {
                if (true == viewModel.showMoreAlias.value){
                    addAll(viewModel.currAliasList.subList(0, viewModel.showMoreAliasCount))
                } else {
                    addAll(viewModel.currAliasList)
                }
                add(getString(R.string.push_add_alias))
            }
            aliasAdapter?.labels?.clear()
            aliasAdapter?.labels?.addAll(showAliasData)
            aliasAdapter?.notifyDataSetChanged()
        }
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
            viewModel.addAlias(it)
        }
    }

    private fun showAccountInputDialog() {
        requireContext().showInputDialog(
            R.string.push_bind_account, R.string.push_bind_account_hint,
            showAlert = false,
            showAliasInput = false
        ) { it, _ ->
            viewModel.bindAccount(it)
        }
    }

    private fun showPhoneInputDialog() {
        requireContext().showInputDialog(
            R.string.push_text_notification, R.string.push_text_notification_hint,
            showAlert = false,
            showAliasInput = false
        ) { it, _ ->
            viewModel.bindPhone(it)
        }
    }
}