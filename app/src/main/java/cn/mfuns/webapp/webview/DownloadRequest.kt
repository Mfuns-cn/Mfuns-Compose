package cn.mfuns.webapp.webview

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response

internal class DownloadRequest(
    url: String,
    listener: Response.Listener<ByteArray>,
    errorListener: Response.ErrorListener?
) : Request<ByteArray?>(Method.GET, url, errorListener) {
    private val listener: Response.Listener<ByteArray>

    override fun parseNetworkResponse(response: NetworkResponse?): Response<ByteArray?> =
        Response.success(response?.data, null)

    override fun deliverResponse(response: ByteArray?) = listener.onResponse(response)

    init {
        this.listener = listener
    }
}
