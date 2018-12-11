package com.example.catherine.directionbuddy.tasks

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class AddDirectionsTask (
        var name: String,
        var address: String,
        var city: String,
        var state: String,
        var zip: String,
        var contact: String,
        var category: String,
        var user_id: Int,
        context: Context): AsyncTask<Unit, Unit, Boolean>() {



    override fun doInBackground(vararg params: Unit?): Boolean {
        var urlConnection: HttpURLConnection? = null
        try {
            var paramString="";
            if(contact == null) {
                paramString = "name=" + name + "&address=" + address + "&city=" + city + "&state=" + state + "&zip" + zip + "&category="+category+"&contact=null&user_id=" + user_id;

            } else {
                paramString = "name=" + name + "&address=" + address + "&city=" + city + "&state=" + state + "&zip" + zip +"&category="+category+ "&contact=" + contact + "&user_id=" + user_id;
                // [{"id":"1", ,"name":"My House","address":"531 Robert Quigley Drive","city":"Scottsville",
                // "state":"NY","zip":"14546","country":"US","contact":null,"user_id":null}]
            }
            //url
            val webServiceURL = URL("http://www.catherinewaltersontheweb.com/projects/directions/AddDirections.php?"+paramString)

            //open connection
            urlConnection = webServiceURL.openConnection() as HttpURLConnection

            val responseCode = urlConnection?.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false
            }
            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()

            try {
                val jsonObj = JSONArray(stringBuilder.toString())
                Log.d("JSON:::", jsonObj.toString())
//                for (json in jsonObj) {
//                for (i in 0..jsonObj.length()) {
//                    val json = jsonObj.getJSONArray(i)
//
//                    Log.d("ID:", json.getString("id"))
//                    // body of loop
//                }
//                for (i in 0..jsonObj.length()) {
//                    jsonObj.get
//                }


//                [{"id":"1","nickname":"My House","name":"My House","address":"531 Robert Quigley Drive","city":"Scottsville","state":"NY","zip":"14546","country":"US","contact":null,"user_id":null}]


//                val coord = jsonObj.getJSONObject("coord")
//                val longitude = coord.getString("lon")
//                val latitude = coord.getString("lat")
//
//                val weatherArray = jsonObj.getJSONArray("weather")
//                val weatherONE = weatherArray.getJSONObject(0)
//                val desc = weatherONE.getString("description")
//                val imageString = weatherONE.getString("icon")
//
//                val mainObj = jsonObj.getJSONObject("main")
//                temperatureString = mainObj.getDouble("temp").toString()
//                humidityString = mainObj.getInt("humidity").toString()
//
//                val sys = jsonObj.getJSONObject("sys")
//                val sunsetL = sys.getLong("sunset")
//                val sunsetTime = Date(sunsetL * 1000L)
//                countryString = sys.getString("country")
//
//                cityString = jsonObj.getString("name")
//

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return true
        } // end try
        catch (e: MalformedURLException) {
            Log.v(TAG, e.toString())
        } // end catch
        catch (e: IOException) {
            Log.v(TAG, e.toString())
        } // end catch
        catch (e: IllegalStateException) {
            Log.v(TAG, e.toString())
        }
        finally {
            urlConnection?.disconnect();
        }
        // end catch
        return false

    }

    //    var resources: Resources? = null
////
//    var dirctionsArr = emptyArray<Objects>()
//    var id: Int? = null
//    var nickname: String? = null
//    var name: String? = null
//    var address: String? = null
//    var city: String? = null
//    var stateString: String? = null
//    var zip: String? = null
//    var country: String? = null
//    var contact: String? = null
//    var userId: String? = null
//
//    init {
//        resources = context.resources
//    }
//
//    // interface for receiver of weather information
//    interface DirectionsListener {
//        fun onDirectionsLoaded( id: Int?,
//                                nickname: String?,
//                                name: String?,
//                                address: String?,
//                                city: String?,
//                                stateString: String?,
//                                zip: String?,
//                                country: String?,
//                                contact: String?,
//                                userId: String?
//                                )
//    } // end interface ForecastListener
//
//
//
//    // update the UI back on the main thread
//    override fun onPostExecute(success: Boolean) {
//        //should make sure forecastString has a value
//        // pass the information to the ForecastListener
//
//        if (success) {
//            weatherForecastListener.onForecastLoaded(iconBitmap,
//                    temperatureString, humidityString,
//                    cityString,
//                    countryString)
//        } else {
//            weatherForecastListener.onForecastLoaded(null,
//                    null, null,
//                    null,
//                    null)
//        }
//    } // end method onPostExecute
//
    companion object {
        private val TAG = "AddDirectionsTask.kt"

    }
} // end GetDirectionsTask
