package com.nishant.myweather

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.os.Bundle
import android.os.ConditionVariable
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nishant.myweather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log

//9e3ba8db5881c7795a0de642240b1a77
class MainActivity : AppCompatActivity() {
    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root )
        fetchWeatherData("Mumbai")
        SearchCity()
    }

    // ye sabh s4arch ke liye codes hai
    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }// ya tak search

    private fun fetchWeatherData(cityNAme:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(Apiinterface::class.java)

        val response = retrofit.getWeatherData(  cityNAme,"9e3ba8db5881c7795a0de642240b1a77", "metric")
        response.enqueue(object : Callback<MyWeather>{
            override fun onResponse(call: Call<MyWeather>, response: Response<MyWeather>) {
                val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null){
                        val temperature = responseBody.main.temp.toString()
                        val humidity = responseBody.main.humidity
                        val wind = responseBody.wind.speed
                        val sunRise = responseBody.sys.sunrise.toLong()
                        val sunSet = responseBody.sys.sunset.toLong()
                        val seaLevel = responseBody.main.pressure
                        val maxtemp = responseBody.main.temp_max
                        val mintemp = responseBody.main.temp_min
                        val condition = responseBody.weather.firstOrNull()?.main?:"unknow"

                        binding.tempa.text= "$temperature °C"
                        binding.weather.text = condition
                        binding.maxtemp.text = "Max Temp: $maxtemp °C"
                        binding.mintemp.text = "Min Temp: $mintemp °C"
                        binding.humidity.text = "$humidity %"
                        binding.wind.text = "$wind m/s"
                        binding.sunrise.text = "${time(sunRise)} "
                        binding.sunset.text = "${time(sunSet)} "
                        binding.sea.text = "$seaLevel hPa"
                        binding.condition.text = condition
                        binding.day.text = dayName(System.currentTimeMillis())
                            binding.date.text = date()
                            binding.cityname.text = "$cityNAme"
                        // Log.d("TAG", "onResponse: $temperature")

                        changeImageAccordingToWeatherCondition(condition)
                    }

            }

            override fun onFailure(call: Call<MyWeather>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }



    private fun changeImageAccordingToWeatherCondition(conditionString: String) {
        when (conditionString) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.summerposter)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Cloud", "Clouds", "Overcast", "Mist", "Foggy " -> {
                binding.root.setBackgroundResource(R.drawable.tuffanposter)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rainposter)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snowposter)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.summerposter)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }

              binding.lottieAnimationView.playAnimation()
        }


     private   fun date():String {
        val sdf = SimpleDateFormat("dd MMM YYY" , Locale.getDefault())
        return  sdf.format(Date())

      }
    private  fun  time(timestamp:Long):String {
        val sdf = SimpleDateFormat("HH:mm" , Locale.getDefault())
        return  sdf.format((Date(timestamp*1000)))

    }

     fun dayName(timestamp:Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return  sdf.format((Date()))
      }
}