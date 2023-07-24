package com.inovego.temanesia.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inovego.temanesia.data.model.UserItem
import com.inovego.temanesia.utils.Event
import com.inovego.temanesia.utils.FIREBASE_USER_MOBILE
import com.inovego.temanesia.utils.cat

class AuthViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDB: FirebaseFirestore,
) : ViewModel() {

    private val _section = MutableLiveData<String>()
    val section: LiveData<String> = _section

    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> = _uid

    private val _isRegistered = MutableLiveData(false)
    val isRegistered: LiveData<Boolean> = _isRegistered

    private val _isSignedIn = MutableLiveData(false)
    val isSignedIn: LiveData<Boolean> = _isSignedIn

    private val _isEmailVerified = MutableLiveData(false)
    val isEmailVerified: LiveData<Boolean> = _isEmailVerified

    private val _isUserDataSaved = MutableLiveData(false)
    val isUserDataSaved: LiveData<Boolean> = _isUserDataSaved

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun setSection(state: String) {
        _section.value = state
    }

    fun setSnackbarText(text: String) {
        _toastText.value = Event(text)
    }

    fun signupFirebase(
        email: String,
        password: String,
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            _isRegistered.value = it.isSuccessful
            if (it.isSuccessful) _toastText.value = Event("Berhasil Membuat Akun")
            else _toastText.value = Event("Gagal : ${it.exception?.message}")
        }.addOnFailureListener { e -> Event("Gagal SignUp : $e") }
    }

    fun signInFirebase(
        email: String,
        password: String,
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            _isSignedIn.value = it.isSuccessful
        }.addOnFailureListener { e ->
            _toastText.value = Event("${e.message}")
        }
    }

    fun checkEmail(): Boolean {
        return if (firebaseAuth.currentUser == null) {
            false
        } else if (firebaseAuth.currentUser?.isEmailVerified == null) {
            _toastText.value = Event("Harap Verifikasi Email Anda")
            firebaseAuth.signOut()
            false
        } else {
            _toastText.value = Event("Berhasil Masuk Akun")
            true
        }
    }

    fun writeDataOnDB(uid: String, userData: UserItem) {
        val users = HashMap<String, Any>()
        users["alamat"] = userData.alamat
        users["email"] = userData.email
        users["jenjang_pendidikan"] = userData.pendidikan
        users["jurusan"] = userData.jurusan
        users["nama"] = userData.nama
        users["nik"] = userData.nik
        users["nim_nisn"] = userData.nimNisn
        users["sekolah"] = userData.sekolah

        firebaseDB.collection(FIREBASE_USER_MOBILE).document(uid).set(users)
            .addOnSuccessListener {
                _isUserDataSaved.value = true
                _toastText.value = Event("Silahkan login seteleh memverifikasi email anda")
                firebaseAuth.signOut()
            }.addOnFailureListener { e ->
                _toastText.value = Event("Gagal Menyimpan Data : $e")
                cat(e.message)
            }
    }


    fun sendEmailVerification() {
        firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                _toastText.value = Event("Berhasil Mengirim Email Verifikasi")
                _uid.value = firebaseAuth.uid
            } else {
                firebaseAuth.signOut()
                _isSignedIn.value = false
                _toastText.value = Event("Gagal Mengirim Email Verifikasi")
            }
        }?.addOnFailureListener { e ->
            _toastText.value = Event("Gagal : $e")
        }
    }
}