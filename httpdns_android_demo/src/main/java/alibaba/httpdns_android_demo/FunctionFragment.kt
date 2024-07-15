package alibaba.httpdns_android_demo

import alibaba.httpdns_android_demo.databinding.FragmentFunctionBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FunctionFragment :Fragment() {

    private var binding:FragmentFunctionBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFunctionBinding.inflate(inflater , container , false)
        binding?.lifecycleOwner = this
        return binding?.root
    }

}