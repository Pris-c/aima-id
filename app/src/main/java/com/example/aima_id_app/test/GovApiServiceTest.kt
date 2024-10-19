package com.example.aima_id_app.test

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aima_id_app.R
import com.example.aima_id_app.data.model.apiModel.AdviceResponse
import com.example.aima_id_app.data.model.apiModel.GovApiRequest
import com.example.aima_id_app.data.model.apiModel.GovApiResponse
import com.example.aima_id_app.data.service.GovApiInterface
import com.example.aima_id_app.data.service.RetrofitClient
import com.example.aima_id_app.databinding.ActivityGovApiServiceTestBinding
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback
import java.time.LocalDate

class GovApiServiceTest : AppCompatActivity() {

    private val binding by lazy { ActivityGovApiServiceTestBinding.inflate(layoutInflater) }
    private lateinit var govApiInterface: GovApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_gov_api_service_test)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //getGovApiService()
        //getAdvice()

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            //val selectedDate = "$dayOfMonth/${month + 1}/$year"
            val data = LocalDate.of(year, month + 1, dayOfMonth)

            //Toast.makeText(this, "Data selecionada: $selectedDate", Toast.LENGTH_SHORT).show()


        }

        /*getCitizenMatch { result ->
            if (result) {
                binding.textViewReponse.text = "Citizen matched"
            } else {
                binding.textViewReponse.text = "Citizen does not match"
            }
        }*/
    }

    val govApiRequest = GovApiRequest(
        name = "Ana Costa",
        nif = "246813579",
        birthdate = "1992-06-05"
    )

    private fun getGovApiService() {
       govApiInterface =  RetrofitClient.retrofitInstance.create(GovApiInterface::class.java)
    }


   /* fun getAdvice(){
        val call = govApiInterface.getAdvice();

        call.enqueue(object : Callback<AdviceResponse> {
            override fun onResponse(call: Call<AdviceResponse>, response: Response<AdviceResponse>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    binding.textViewReponse.setText(post.toString())
                } else {
                    Toast.makeText(applicationContext, "Erro ao printar body", Toast.LENGTH_SHORT).show()
                    // Handle error
                }
            }

            override fun onFailure(call: Call<AdviceResponse>, t: Throwable) {
              Toast.makeText(applicationContext, "Erro na requisição", Toast.LENGTH_SHORT).show()
            }
        })
    }
*/


  fun getCitizenMatch(callback: (Boolean) -> Unit) {

        Toast.makeText(this, govApiRequest.toString(), Toast.LENGTH_LONG).show()

        val call = govApiInterface.verifyCitizen(govApiRequest)

        call.enqueue(object : Callback<GovApiResponse> {
            override fun onResponse(call: Call<GovApiResponse>, response: Response<GovApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val match = response.body()?.match ?: false
                    Toast.makeText(applicationContext, "Request success", Toast.LENGTH_SHORT).show()
                    callback(match)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<GovApiResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Request fail", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
                callback(false)
            }
        })
    }

}