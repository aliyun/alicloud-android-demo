package alibaba.httpdns_android_demo

import alibaba.httpdns_android_demo.databinding.DialogInputBinding
import alibaba.httpdns_android_demo.databinding.HttpdnsHostResolveAlertBinding
import alibaba.httpdns_android_demo.databinding.OkHttpResponseDialogBinding
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.alibaba.sdk.android.httpdns.Region
import com.alibaba.sdk.android.httpdns.ranking.IPRankingBean
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.util.regex.Pattern

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

fun convertHostListToStr(hosts: List<String>?): String? {
    if (hosts == null) {
        return null
    }
    val array = JSONArray()
    for (host in hosts) {
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

/**
 * 弹出输入框
 */
fun Context.showInputDialog(title:String , inputType:Int , callback:(String) -> Unit){
    if (mAlertDialog != null && this === mAlertDialog?.context && true == mAlertDialog?.isShowing) {
        return
    }
    if (mAlertDialog == null || this != mAlertDialog?.context) {
        mAlertDialog = AlertDialog.Builder(this , R.style.Theme_AppCompat_Dialog_Alert).create()
    }

    val binding =  DialogInputBinding.inflate(LayoutInflater.from(this) , null , false)
    binding.title = title
    binding.etInput.inputType = inputType
    binding.tvCancel.setOnClickListener { mAlertDialog?.dismiss() }
    binding.tvConfirm.setOnClickListener {
        val inputText = binding.etInput.text.toString().trim()
        if (TextUtils.isEmpty(inputText)) {
            Toast.makeText(this , "输入内容为空" , Toast.LENGTH_SHORT).show()
        }else {
            callback.invoke(inputText)
            mAlertDialog?.dismiss()
        }
    }
    mAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    mAlertDialog?.setView(binding.root)
    mAlertDialog?.show()

}

/**
 * 展示首页点击开始解析之后的提示弹窗
 */
fun Context.showHostResolveAlert(host:String , callback:() -> Unit){
    if (mAlertDialog != null && this === mAlertDialog?.context && true == mAlertDialog?.isShowing) {
        return
    }
    if (mAlertDialog == null || this != mAlertDialog?.context) {
        mAlertDialog = AlertDialog.Builder(this , R.style.Theme_AppCompat_Dialog_Alert).create()
    }

    val binding =  HttpdnsHostResolveAlertBinding.inflate(LayoutInflater.from(this) , null , false)
    binding.host = host
    binding.tvKnow.setOnClickListener {
        mAlertDialog?.dismiss()
    }
    mAlertDialog?.setOnDismissListener { callback() }
    mAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    mAlertDialog?.setView(binding.root)
    mAlertDialog?.show()

}

/**
 * 展示最佳实践OkHttp请求结果的弹窗
 */
fun Context.showOkHttpResponseAlert(title:String , responseStr:String){
    if (mAlertDialog != null && this === mAlertDialog?.context && true == mAlertDialog?.isShowing) {
        return
    }
    if (mAlertDialog == null || this != mAlertDialog?.context) {
        mAlertDialog = AlertDialog.Builder(this , R.style.Theme_AppCompat_Dialog_Alert).create()
    }
    val binding =  OkHttpResponseDialogBinding.inflate(LayoutInflater.from(this) , null , false)
    binding.title = title
    binding.responseStr = responseStr
    binding.tvKnow.setOnClickListener {
        mAlertDialog?.dismiss()
    }
    mAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    mAlertDialog?.setView(binding.root)
    mAlertDialog?.show()

}

fun regionToText(region: Region):String {
    return when(region) {
        Region.DEFAULT -> RegionText.REGION_TEXT_CHINA
        Region.HK -> RegionText.REGION_TEXT_HK
        Region.SG -> RegionText.REGION_TEXT_SG
        Region.DE -> RegionText.REGION_TEXT_DE
        Region.US -> RegionText.REGION_TEXT_US
        else -> RegionText.REGION_TEXT_CHINA
    }
}

/**
 * region 枚举和文字之间的转换
 */
fun textToRegion(region:String):Region {
    return when(region){
        RegionText.REGION_TEXT_CHINA -> Region.DEFAULT
        RegionText.REGION_TEXT_HK -> Region.HK
        RegionText.REGION_TEXT_SG -> Region.SG
        RegionText.REGION_TEXT_DE -> Region.DE
        RegionText.REGION_TEXT_US -> Region.US
        else -> Region.DEFAULT
    }
}

/**
 * 获取控制台配置的域名列表
 */
fun readControlHostConfig(): MutableList<String> {
    val controlHost = mutableListOf<String>()
    val controlHostJson = Config.CONTROL_HOST_JSON

    if (TextUtils.isEmpty(controlHostJson)) {
        return controlHost
    }
    val jsonObj = JSONObject(controlHostJson)
    if (jsonObj.has(KEY_DOMAINS)) {
        val jsonArrayControlHost = jsonObj.optJSONArray(KEY_DOMAINS) ?: return controlHost
        for ( i in 0 until jsonArrayControlHost.length()) {
            controlHost.add(jsonArrayControlHost.optString(i))
        }
    }
    return controlHost
}

object RegionText {
    const val REGION_TEXT_CHINA = "中国大陆"
    const val REGION_TEXT_HK = "中国香港"
    const val REGION_TEXT_SG = "新加坡"
    const val REGION_TEXT_DE = "德国"
    const val REGION_TEXT_US = "美国"
}

fun Context.getStatusBarHeight():Int{
    val statusBarId = resources.getIdentifier("status_bar_height" , "dimen" , "android")
    return if (statusBarId > 0) {
        resources.getDimensionPixelSize(statusBarId)
    }else {
        22.toDp()
    }
}

/**
 * 域名正则判断
 */
fun isValidHost(host: String): Boolean {
    val pattern = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9-]{1,61}[a-zA-Z0-9]\\.[a-zA-Z0-9-]{1,61}\\.[a-zA-Z0-9-]{1,61}$")
    val matcher = pattern.matcher(host)
    return matcher.matches()
}
