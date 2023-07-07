package com.inovego.temanesia

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.inovego.temanesia.data.model.UserItem
import com.inovego.temanesia.databinding.ActivityMainBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.auth.AuthViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val navController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navHostFragment.navController
    }
    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_discover,
                R.id.navigation_discuss,
                R.id.navigation_profile
            )
        )
    }

    val viewModel : AuthViewModel by viewModels {
        ViewModelFactory.getInstance()
    }

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navBottomView.setupWithNavController(navController)

        db.collection(FIREBASE_COLLECTION)
            .document(FIREBASE_DOCUMENT)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val documentData = documentSnapshot.data?.let { studentData ->
                        UserItem(
                            alamat = studentData["alamat"] as String,
                            email = studentData["email"] as String,
                            pendidikan = studentData["jenjang_pendidikan"] as String,
                            jurusan = studentData["jurusan"] as String,
                            nama = studentData["nama"] as String,
                            nik = studentData["nik"] as Long,
                            nimNisn = studentData["nim_nisn"] as Long,
                            sekolah = studentData["sekolah"] as String
                        )
                    }
                    cat(documentData)
                } else {
                    cat("Document does not exist.")
                }
            }
            .addOnFailureListener { exception ->
                println("Error retrieving document: $exception")
            }

    }

    companion object {
        internal const val FIREBASE_COLLECTION = "users_mobile"
        private const val FIREBASE_DOCUMENT = "vrdkLDpJBP2RfWnHYE2h"
    }
}