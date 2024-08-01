package alibaba.httpdns_android_demo.view

import alibaba.httpdns_android_demo.toDp
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class CaseItemDecoration : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val currPosition = parent.getChildLayoutPosition(view)
        outRect.set(
            if (0 != currPosition) {
                12.toDp()
            } else {
                0
            }, 0, 0, 0
        )
    }

}