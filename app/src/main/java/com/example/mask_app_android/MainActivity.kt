/*以下這一行的作用是指出這個檔案所在的命名空間，package(套件)是關鍵字,
如果我們寫了一個java檔案,其他的檔案要引用到他的class或class內的方法,就需要*/
package com.example.mask_app_android

/*以下為預設library*/
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//以下為新增library
import android.util.Log
import okhttp3.*
import com.example.mask_app_android.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    /*Bundle型別正是我們前面所導入的package之一，Bundle的內容與Android手機平臺的記憶體管理有關，
    Bundle類別可以保存Activity上一次關閉(stop)時的狀態，我們可以透過覆載onStop方法來保存關閉前的狀態，
    當程式啟動時，會再次呼叫onCreate方法，就能從savedInstanceState中得到前一次凍結的狀態。
    我們也可以透過Bundle來將這個Activity的內容傳到下一個Activity中。*/
    override fun onCreate(savedInstanceState: Bundle?)
    {
        /*意思是執行AppCompatActivity類別中onCreate方法的內容，
        因為我們已經覆載(@Override)了MainActivity類別的onCreate方法，
        因此如果我們想將原本的onCreate方法內容保留，再加上我們自己的內容，
        就要使用super語句，並傳入savedInstanceState參數*/
        super.onCreate(savedInstanceState)

        /*setContentView就是螢幕顯示的畫面，是透過各種介面元件的排列配置結構來描述的。
        要將一套版面配置的層次結構轉換到一個螢幕上時，Activity會呼叫它用來設定螢幕顯示內容的setContentView方法，
        並傳入定義了版面配置的Xml描述檔。當Activity被啟動並需要顯示到螢幕上時，
        系統會通知Activity並根據引用的Xml檔來描繪出使用者介面。*/
        //setContentView(R.layout.activity_main)    //因為新版kotlin修改掉synthetic所以不需要這行，由下面兩行取代
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPharmacyData()
    }


    private fun getPharmacyData()
    {
        //口罩資料網址
        val pharmaciesDataUrl =
            "https://raw.githubusercontent.com/thishkt/pharmacies/master/data/info.json"

        //Part 1: 宣告 OkHttpClient
        val okHttpClient = OkHttpClient().newBuilder().build()

        //Part 2: 宣告 Request，要求要連到指定網址
        val request: Request = Request.Builder().url(pharmaciesDataUrl).get().build()

        //Part 3: 宣告 Call
        val call = okHttpClient.newCall(request)

        //執行 Call 連線後，採用 enqueue 非同步方式，獲取到回應的結果資料
        /*enqueue就是禁止以下程式在主執行緒(main thread)下運行，原因是這樣耗時的工作應該另外開一條執行緒才不會卡住*/
        call.enqueue(object : Callback
        {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException)
            {Log.d("HKT", "onFailure: $e")}


            //以下部分是我們跟url做request後得到的response
            override fun onResponse(call: okhttp3.Call, response: Response)
            {
                /*response.body?.string()就是我們從url的server取得的資料，切記不可重複拿兩次以上，
                會報error，另外直接print response是沒有資料的，必須透過body?.string的轉換才可以*/
                val pharmacyData = response.body?.string()
                //Log.d("HKT", "onResponse: $pharmacyData")//原本pharmacy_data是-->{response.body?.string()}

                /*要加上runOnUiThread是因為取得資料是在背景的環境下，而顯示要顯示在主要畫面，
                而處理主要畫面就是main thread的事情，因此必須要加上這個code做資料的轉換，才能顯示在畫面上*/
                runOnUiThread{
                    binding.tvPharmacyData.text = pharmacyData
                }

            }

        })

    }
}