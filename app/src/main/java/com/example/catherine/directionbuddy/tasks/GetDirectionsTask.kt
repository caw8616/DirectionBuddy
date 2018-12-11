package com.example.catherine.directionbuddy.tasks

import android.content.res.Resources
import android.os.AsyncTask
import android.util.Log
import com.example.catherine.directionbuddy.entities.Direction
import com.example.catherine.directionbuddy.viewmodels.AllDirectionsViewModel
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList


class GetDirectionsTask ( var user_id: Int, var directionsListener: DirectionsListener ): AsyncTask<Unit, Unit, Boolean>() {

    var resources: Resources? = null
    var directionsArr = ArrayList<Direction>()
    var directionsViewModel : AllDirectionsViewModel? =  null

//    init {
//        resources = context.resources
//    }

    interface DirectionsListener {
        fun onDirectionsLoaded( directionsArr: ArrayList<Direction>)
    }


    override fun doInBackground(vararg params: Unit?): Boolean {
        var urlConnection: HttpURLConnection? = null
        try {
            Log.d("API",user_id.toString())

            //url
            val webServiceURL = URL("http://www.catherinewaltersontheweb.com/projects/directions/GetDirectionsId.php?id="+user_id)

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
                val jsonArr = JSONArray(stringBuilder.toString())
                Log.d("JSON:::", jsonArr.toString())
                for (i in 0..(jsonArr.length() - 1)) {
                    val item = jsonArr.getJSONObject(i)
                    Log.d("JSON OBJ:::", item.toString())
                    val direction = Direction(
                            id = item.getInt("id"),
                            name = item.getString("name"),
                            address = item.getString("address"),
                            city = item.getString("city"),
                            state = item.getString("state"),
                            zip = item.getString("zip"),
                            contact = item.getString("contact"),
                            category = item.getString("category"),
                            user_id = item.getString("user_id")!!)
//                    directionsViewModel!!.insertDirection(direction = direction)

                    directionsArr.add(direction)
                }
                Log.d("API",directionsArr.toString())
//                directionsViewModel!!.insertAll(directionsArr)
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



    // update the UI back on the main thread
    override fun onPostExecute(success: Boolean) {

        if (success) {
            directionsListener.onDirectionsLoaded(directionsArr)
        } else {
//            directionsListener.onDirectionsLoaded(null,
//                    null, null,
//                    null,
//                    null)
        }
    } // end method onPostExecute

    companion object {
        private val TAG = "GetDirectionsTask.kt"

    }
} // end GetDirectionsTask

