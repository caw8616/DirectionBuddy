package com.example.catherine.directionbuddy.tasks

import android.content.res.Resources
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
import java.util.ArrayList


//class GetDirectionsTask ( var user_id: Int, var directionsListener: DirectionsListener ): AsyncTask<Unit, Unit, Boolean>() {

//    var resources: Resources? = null
//    var directionsArr = ArrayList<MyDirection>()
//
////    init {
////        resources = context.resources
////    }
//
//    interface DirectionsListener {
//        fun onDirectionsLoaded( directionsArr: ArrayList<MyDirection>)
//    }
//
//
//    override fun doInBackground(vararg params: Unit?): Boolean {
//        var urlConnection: HttpURLConnection? = null
//        try {
//            //url
//            val webServiceURL = URL("http://www.catherinewaltersontheweb.com/projects/directions/GetDirectionsId.php?id="+user_id)
//
//            //open connection
//            urlConnection = webServiceURL.openConnection() as HttpURLConnection
//
//            val responseCode = urlConnection?.responseCode
//            if (responseCode != HttpURLConnection.HTTP_OK) {
//                return false
//            }
//            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
//            val stringBuilder = StringBuilder()
//            var line: String? = bufferedReader.readLine()
//            while (line != null) {
//                stringBuilder.append(line).append("\n")
//                line = bufferedReader.readLine()
//            }
//            bufferedReader.close()
//
//            try {
//                val jsonArr = JSONArray(stringBuilder.toString())
//                Log.d("JSON:::", jsonArr.toString())
//                for (i in 0..(jsonArr.length() - 1)) {
//                    val item = jsonArr.getJSONObject(i)
//                    Log.d("JSON OBJ:::", item.toString())
//                    var direction = MyDirection(
//                            item.getInt("id"),
//                            item.getString("name"),
//                            item.getString("address"),
//                            item.getString("city"),
//                            item.getString("state"),
//                            item.getString("zip"),
//                            item.getString("contact"),
//                            item.getInt("user_id")
//                    )
//                    directionsArr.add(direction)
//                }
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//            return true
//        } // end try
//        catch (e: MalformedURLException) {
//            Log.v(TAG, e.toString())
//        } // end catch
//        catch (e: IOException) {
//            Log.v(TAG, e.toString())
//        } // end catch
//        catch (e: IllegalStateException) {
//            Log.v(TAG, e.toString())
//        }
//        finally {
//            urlConnection?.disconnect();
//        }
//        // end catch
//        return false
//    }
//
//
//
//    // update the UI back on the main thread
//    override fun onPostExecute(success: Boolean) {
//
//        if (success) {
//            directionsListener.onDirectionsLoaded(directionsArr)
//        } else {
////            directionsListener.onDirectionsLoaded(null,
////                    null, null,
////                    null,
////                    null)
//        }
//    } // end method onPostExecute
//
//    companion object {
//        private val TAG = "GetDirectionsTask.kt"
//
//    }
//} // end GetDirectionsTask

