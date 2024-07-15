package alibaba.httpdns_android_demo

import alibaba.httpdns_android_demo.databinding.FragmentCaseBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class CaseFragment:Fragment() {

    private var binding:FragmentCaseBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCaseBinding.inflate(inflater , container , false)
        binding?.lifecycleOwner = this
        return binding?.root
    }

}