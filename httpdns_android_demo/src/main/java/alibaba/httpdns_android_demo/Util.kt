package alibaba.httpdns_android_demo

import alibaba.httpdns_android_demo.databinding.HttpdnsDialogInputBinding
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.alibaba.sdk.android.httpdns.ranking.IPRankingBean
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException

/**
 * @author allen.wy
 * @date 2023/6/5
 */
fun String?.toHostList(): MutableList<String>? {
    if (this == null) {
        return null
    }
    try {
        val array = JSONArray(this)
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return null
}

fun String?.toIPRankingList(): MutableList<IPRankingBean>? {
    if (this == null) {
        return null
    }
    try {
        val jsonObject = JSONObject(this)
        val list = mutableListOf<IPRankingBean>()
        val it = jsonObject.keys()
        while (it.hasNext()) {
            val host = it.next()
            list.add(
                IPRankingBean(
                    host,
                    jsonObject.getInt(host)
                )
            )
        }
        return list
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return null
}

fun String?.toTtlCacheMap(): MutableMap<String, Int>? {
    if (this == null) {
        return null
    }
    try {
        val jsonObject = JSONObject(this)
        val map = mutableMapOf<String, Int>()
        val it = jsonObject.keys()
        while (it.hasNext()) {
            val host = it.next()
            val ttl = jsonObject.getInt(host)
            map[host] = ttl
        }
        return map
    } catch (e: JSONException) {
        e.printStackTrace()
    }

    return null
}

fun getAccountPreference(context: Context): SharedPreferences {
    return context.getSharedPreferences(
        "aliyun_httpdns_${Config.ACCOUNT_ID}",
        Context.MODE_PRIVATE
    )
}

fun convertPreResolveList(preResolveHostList: List<String>?): String? {
    if (preResolveHostList == null) {
        return null
    }
    val array = JSONArray()
    for (host in preResolveHostList) {
        array.put(host)
    }
    return array.toString()
}

@Throws(IOException::class)
fun readStringFrom(streamReader: BufferedReader): StringBuilder {
    val sb = StringBuilder()
    var line: String?
    while (streamReader.readLine().also { line = it } != null) {
        sb.append(line)
    }
    return sb
}

fun Int.toDp(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
}

private var mAlertDialog:AlertDialog? = null

fun Context.showInputDialog(title:String , inputType:Int , callback:(String) -> Unit){
    if (mAlertDialog != null && this === mAlertDialog?.context && true == mAlertDialog?.isShowing) {
        return
    }
    if (mAlertDialog == null || this != mAlertDialog?.context) {
        mAlertDialog = AlertDialog.Builder(this , R.style.Theme_AppCompat_Dialog_Alert).create()
    }

    val binding =  HttpdnsDialogInputBinding.inflate(LayoutInflater.from(this) , null , false)
    binding.title = title
    binding.etInput.inputType = inputType
    binding.tvCancel.setOnClickListener { mAlertDialog?.dismiss() }
    binding.tvConfirm.setOnClickListener {
        callback.invoke(binding.etInput.text.toString().trim())
        mAlertDialog?.dismiss()
    }
    mAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    mAlertDialog?.setView(binding.root)
    mAlertDialog?.show()

}
