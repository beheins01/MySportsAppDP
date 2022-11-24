package com.example.mysportsapp

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
// API Client for fetching and parsing data
class TeamRepository {
    val baseURL= "https://www.thesportsdb.com/api/v1/json/50130162"

    var client = OkHttpClient()

    // search teams function by query
    fun searchTeams(term: String): List<Team> {
        println("Searching teams $term")
        // Build request to hit /searchTeams?t=MySearchTeam
        val request = Request.Builder()
            .url("$baseURL/searchTeams.php?t=$term")
            .build();
        try {
            // Execute
            val response = client.newCall(request).execute()
            val data = response.body?.string()
            println("Teams Result $data")
            /*
            Data returns in the format
            { team: [
                Team1,
                Team2,
                ...
                ]
                }
             */
            // We Prase Object first, get Teams Array,
            val json = JSONObject(data).getJSONArray("teams")
            var teams = mutableListOf<Team>()
            // Loop teams array and build Team classes
            for (i in 0 until json.length()) {
                val j = json.getJSONObject(i);
                teams.add(
                    Team(
                        j.getString("idTeam"),
                        j.getString("strTeam"),
                        j.getString("strTeamShort"),
                        j.getString("strSport"),
                        j.getString("strLeague"),
                        j.getString("strTeamBanner")
                    )
                )
            }
            return teams
        } catch (err: Throwable) {
            println("Fetchh teams error $err")
        }
        return mutableListOf<Team>()
    }
}