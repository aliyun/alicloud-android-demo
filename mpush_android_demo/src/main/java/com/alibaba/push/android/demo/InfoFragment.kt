package com.alibaba.push.android.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alibaba.push.android.demo.databinding.InfoFragmentBinding
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory

/**
 * 信息Fragment
 * @author ren
 * @date 2024/09/20
 */
class InfoFragment : Fragment() {

    private lateinit var binding: InfoFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val service = PushServiceFactory.getCloudPushService()
        binding.tvAppKey.text = requireContext().getAppMetaData("com.alibaba.app.appkey")
        binding.tvAppSecret.text = requireContext().getAppMetaData("com.alibaba.app.appsecret")
        binding.tvDeviceId.text = service.deviceId
        binding.tvUTDID.text = service.utDeviceId
        binding.tvSDKVersion.text = String.format(
            requireContext().getString(R.string.push_version),
            BuildConfig.PUSH_VERSION
        )
        binding.tvPackage.text = requireContext().packageName
    }
}