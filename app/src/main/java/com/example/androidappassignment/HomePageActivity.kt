package com.example.androidappassignment

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home_page.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomePageActivity : AppCompatActivity(), NewsItemClicked {

    private lateinit var mAdapter: NewsListAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        recycler.layoutManager = LinearLayoutManager(this)
        fetchData()
        mAdapter = NewsListAdapter(this)
        recycler.adapter = mAdapter

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.logOut).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    val newsArray = ArrayList<News>()
    private fun fetchData() {
        val url =
            "https://newsapi.org/v2/top-headlines?country=in&apiKey=1e98485ac66846ac8c9043efee2fe248"
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, {
            val newsJsonArray = it.getJSONArray("articles")
            for (i in 0 until newsJsonArray.length()) {
                val newsJsonObject = newsJsonArray.getJSONObject(i)
                val news = News(
                    newsJsonObject.getString("title"),
                    newsJsonObject.getString("description"),
                    newsJsonObject.getString("publishedAt"),
                    newsJsonObject.getString("author"),
                    newsJsonObject.getString("urlToImage"),
                    newsJsonObject.getString("url")
                )
                newsArray.add(news)
            }

            mAdapter.updateNews(newsArray)
        }, {
            Toast.makeText(
                this,
                "Seems like you are not connected with the Internet. Please, Check back your connection!",
                Toast.LENGTH_LONG
            ).show()
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<News>()
            for (i in newsArray) {
                if (i.title.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
            } else {
                mAdapter.setFilteredList(filteredList)
            }
        }
    }

    override fun onItemClicked(item: News) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }
}