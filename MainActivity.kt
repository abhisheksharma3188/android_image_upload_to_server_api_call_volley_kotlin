package com.example.imageanddatauploadvolleykotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.io.IOException


class MainActivity : AppCompatActivity() {
    lateinit var imageView: ImageView
    lateinit var numberEditText: EditText
    lateinit var chooseImageButton: Button
    lateinit var hitApiButton: Button
    lateinit var responseTextView: TextView
    val IMAGE_PICK_CODE = 999
    lateinit var inputNumberString: String
    private var imageData: ByteArray? = null
    private val postURL: String = "https://www.highdip.com/android-test-api/api2.php" // remember to use your own api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        numberEditText = findViewById(R.id.numberEditText)
        chooseImageButton = findViewById(R.id.chooseImageButton)
        hitApiButton = findViewById(R.id.hitApiButton)
        responseTextView = findViewById(R.id.responseTextView)

        chooseImageButton.setOnClickListener {
            chooseImage()
        }

        hitApiButton.setOnClickListener {
            inputNumberString=numberEditText.text.toString()
            hitApi()
        }
    }
    private fun chooseImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun hitApi() {
        imageData?: return
        val request = object : VolleyFileUploadRequest(
            Request.Method.POST,
            postURL,
            Response.Listener{
                println("response is: $it")
                //val jsonArray = JSONArray(it)
                var jsonArray= JSONArray(it.data.decodeToString())
                responseTextView.text=jsonArray.getJSONObject(0).getString("jwt_token")
                Toast.makeText(this,jsonArray.getJSONObject(0).getString("name"), Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener {
                println("error is: $it")
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["imageFile"] = FileDataPart("image", imageData!!, "jpeg")
                return params
            }
            override fun getParams(): Map<String, String>? {
                // below line we are creating a map for storing
                // our values in key and value pair.
                val params: MutableMap<String, String> = HashMap()
                // on below line we are passing our key
                // and value pair to our parameters.
                params["number1"] = inputNumberString
                // at last we are
                // returning our params.
                return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer 1234567890"
                return headers
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            if (uri != null) {
                imageView.setImageURI(uri)
                createImageData(uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
