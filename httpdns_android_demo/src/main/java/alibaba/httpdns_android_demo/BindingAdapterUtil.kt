package alibaba.httpdns_android_demo

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter

class BindingAdapterUtil {

    companion object {

        @JvmStatic
        @BindingAdapter(value = ["regionIcon"])
        fun setRegionIcon(view: ImageView, icon: Int) {
            view.setImageResource(icon)
        }

        @JvmStatic
        @BindingAdapter(value = ["pressed"])
        fun setChecked(view: TextView, pressed: Boolean) {
            view.isPressed = pressed
        }


    }

}