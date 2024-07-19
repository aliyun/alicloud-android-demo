package alibaba.httpdns_android_demo.function

import alibaba.httpdns_android_demo.databinding.FunctionBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * 功能页面
 * @author 任伟
 * @date 2024/07/19
 */
class FunctionFragment :Fragment() {

    private var binding:FunctionBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FunctionBinding.inflate(inflater , container , false)
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

}