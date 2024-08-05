package alibaba.httpdns_android_demo

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter

class BindingAdapterUtil {

    companion object {

        @JvmStatic
        @BindingAdapter(value = ["regionIcon"])
        fun setRegionIcon(view: TextView, drawable: Drawable) {
            drawable.setBounds(0 , 0 , drawable.minimumWidth , drawable.minimumHeight)
            view.setCompoundDrawables(null , null , drawable , null)
        }

        @JvmStatic
        @BindingAdapter(value = ["pressed"])
        fun setChecked(view: TextView, pressed: Boolean) {
            view.isPressed = pressed
        }


    }

}