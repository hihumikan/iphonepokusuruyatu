package net.qqey


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.json.JSONObject
import android.content.Intent
import android.widget.Toast
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text: TextView = findViewById(R.id.textTop)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    val IntentGeter = intent.getStringExtra("android.intent.extra.TEXT")
                    if (IntentGeter != null) {
                        val IntentFilter =
                            "https://www.googleapis.com/youtube/v3/videos?id=" + IntentGeter.substring(
                                17
                            ) + "&key=[YOURAPIKEY]&part=snippet"
                        val httpAsync = IntentFilter
                            .httpGet()
                            .responseString { _, _, result ->
                                when (result) {
                                    is Result.Failure -> {
                                        val ex = result.getException()
                                        println(ex)
                                    }
                                    is Result.Success -> {
                                        val data = result.get()
                                        val parentJsonObj =
                                            JSONObject(data).getJSONArray("items").getJSONObject(0)
                                                .getJSONObject("snippet").getString("title")
                                        text.text = "$parentJsonObj $IntentGeter @YouTube より"
                                        val clip: ClipData =
                                            ClipData.newPlainText("YoutubeCopy", text.text)
                                        clipboard.setPrimaryClip(clip)
                                        val pm = packageManager
                                        val intent =
                                            pm.getLaunchIntentForPackage("com.twitter.android")
                                        try {
                                            startActivity(intent)
                                            finishAffinity();
                                            System.exit(0);
                                        } catch (e: Exception) {
                                            Toast.makeText(this, "対象のアプリがありません", Toast.LENGTH_SHORT)
                                                .show()
                                            finishAffinity();
                                            System.exit(0);
                                        }
                                    }
                                }
                            }
                        httpAsync.join()
                    }
                }
            }
        }
    }
}