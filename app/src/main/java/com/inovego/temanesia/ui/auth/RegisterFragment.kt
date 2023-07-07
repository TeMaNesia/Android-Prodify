package com.inovego.temanesia.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.inovego.temanesia.MainActivity.Companion.FIREBASE_COLLECTION
import com.inovego.temanesia.R
import com.inovego.temanesia.data.model.UserItem
import com.inovego.temanesia.databinding.FragmentRegisterBinding
import com.inovego.temanesia.databinding.ItemsTextFieldBinding
import com.inovego.temanesia.gone
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.hideError
import com.inovego.temanesia.isEmailValid
import com.inovego.temanesia.showError
import com.inovego.temanesia.visible


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    private val viewModel: AuthViewModel by activityViewModels {
        ViewModelFactory.getInstance()
    }
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val txtField: ItemsTextFieldBinding = binding.register.authTextFieldList
        val button = binding.register.authButton
        viewModel.setSection(NIM_NISN_SCHOOL)
        viewModel.section.observe(viewLifecycleOwner) { section ->
            when (section) {
                NIM_NISN_SCHOOL -> {
                    txtField.apply {
                        nimNisnTextFieldContainer.apply {
                            setTextChangeListener(nimNisnTxtField)
                            visible()
                        }
                        sekolahTextFieldContainer.visible()
                        jurusanTextFieldContainer.visible()

                        binding.register.authButton.setOnClickListener {
//                            viewModel.isFieldError(nimNisnTxtField.text)
                            if (nimNisnTxtField.text.isNullOrEmpty() || kampusSekolahTxtField.text.isNullOrEmpty() || jurusanTxtField.text.isNullOrEmpty())
                                setContainerError(NIM_NISN_SCHOOL)
                            else viewModel.setSection(NAME_NIK_ADDRESS)
                        }
                    }
                }

                NAME_NIK_ADDRESS -> {
                    txtField.apply {
                        nimNisnTextFieldContainer.gone()
                        sekolahTextFieldContainer.gone()
                        jurusanTextFieldContainer.gone()

                        namaTextFieldContainer.visible()
                        nikTextFieldContainer.visible()

                        button.setOnClickListener {
                            if (namaLengkapTextField.text.isNullOrEmpty() || nikTextField.text.isNullOrEmpty())
                                setContainerError(NAME_NIK_ADDRESS)
                            else viewModel.setSection(EMAIL_PASSWORD)
                        }
                    }

                }

                EMAIL_PASSWORD -> {
                    txtField.apply {
                        namaTextFieldContainer.gone()
                        nikTextFieldContainer.gone()

                        emailTextFieldContainer.visible()
                        passwordTextFieldContainer.visible()
                        konfirmasiPasswordTextFieldContainer.visible()

                        button.setOnClickListener {
                            val email = emailTextField.text.toString()
                            val password = passwordTxtField.text.toString()
                            val confirmPassword = konfirmasiPasswordTxtField.text.toString()
                            signupFirebase(false, email, password, confirmPassword)
                        }
                    }
                }

                CONFIRM_ACCOUNT -> {
                    txtField.apply {
                        emailTextFieldContainer.gone()
                        passwordTextFieldContainer.gone()
                        konfirmasiPasswordTextFieldContainer.gone()

                        button.setOnClickListener {
                            val email = emailTextField.text.toString()
                            val password = passwordTxtField.text.toString()
                            val confirmPassword = konfirmasiPasswordTxtField.text.toString()
                            val userItemData = setUserData(this)

                            signupFirebase(true, email, password, confirmPassword)
                            viewModel.isRegistered.observe(viewLifecycleOwner) { registered ->
                                if (registered) signInFirebase(email, password)
                            }

                            viewModel.isSignedIn.observe(viewLifecycleOwner) { signedIn ->
                                if (signedIn) writeDataOnDB(userItemData)
                            }

                            viewModel.isUserDataSaved.observe(viewLifecycleOwner) { dataSaved ->
                                if (dataSaved) findNavController().navigate(R.id.action_navigation_register_to_navigation_login)
                            }
                        }
                    }
                }
            }
        }

    }

    private fun sendEmailVerification(currentUser: FirebaseUser) {
        currentUser.sendEmailVerification().addOnCompleteListener {
            if (it.isSuccessful) {
                viewModel.setIsLoggedIn(true)
                Toast.makeText(
                    requireActivity(), "Berhasil Mengirim Email !", Toast.LENGTH_SHORT
                ).show()
            }
            else viewModel.setIsLoggedIn(false)
        }
    }

    private fun signInFirebase(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                viewModel.setUID(it.result.user!!.uid)
                sendEmailVerification(it.result.user!!)

                Toast.makeText(
                    requireActivity(), "Berhasil Mendaftar !", Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireActivity(), it.exception?.message.toString(), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setUserData(txtField: ItemsTextFieldBinding): UserItem {
        val email = txtField.emailTextField.text.toString()
        val alamat = txtField.alamatTextField.text.toString()
        val pendidikan = txtField.kampusSekolahTxtField.text.toString()
        val jurusan = txtField.jurusanTxtField.text.toString()
        val nama = txtField.namaLengkapTextField.text.toString()
        val nik = txtField.nikTextField.text.toString().toLong()
        val nimNisn = txtField.nimNisnTxtField.text.toString().toLong()
        val sekolah = txtField.kampusSekolahTxtField.text.toString()

        return UserItem(
            alamat = alamat,
            email = email,
            pendidikan = pendidikan,
            jurusan = jurusan,
            nama = nama,
            nik = nik,
            nimNisn = nimNisn,
            sekolah = sekolah
        )
    }

    private fun signupFirebase(
        isSignUp: Boolean,
        email: String,
        password: String,
        confirmPassword: String,
    ) {
        val isNotEmpty = checkSignUp(email, password, confirmPassword)
        if (isSignUp && isNotEmpty) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    viewModel.setIsRegistered(true)
                } else {
                    Toast.makeText(
                        requireContext(), it.exception?.message.toString(), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else if (isNotEmpty) {
            viewModel.setSection(CONFIRM_ACCOUNT)
        }
    }

    private fun writeDataOnDB(userData: UserItem) {
        val users = HashMap<String, Any>()
        users["alamat"] = userData.alamat
        users["email"] = userData.email
        users["jenjang_pendidikan"] = userData.pendidikan
        users["jurusan"] = userData.jurusan
        users["nama"] = userData.nama
        users["nik"] = userData.nik
        users["nim_nisn"] = userData.nimNisn
        users["sekolah"] = userData.sekolah


        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            viewModel.setUID(currentUser.uid)
            viewModel.uid.observe(viewLifecycleOwner) { uid ->
                db.collection(FIREBASE_COLLECTION).document(uid).set(users).addOnSuccessListener {
                    viewModel.setIsUserDataSaved(true)
                    firebaseAuth.signOut()
                    Toast.makeText(
                        requireActivity(), "Berhasil Memasukkan data !", Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        requireActivity(), "Error writing document $e", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkSignUp(email: String, password: String, confirmPassword: String): Boolean {
        return if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            setContainerError(EMAIL_PASSWORD, email, password, confirmPassword)

            email.isEmailValid() && password.length >= 8 && password == confirmPassword
        } else {
            setContainerError(EMAIL_PASSWORD, email, password, confirmPassword)

            false
        }
    }

    private fun setContainerError(
        section: String,
        email: String? = null,
        password: String? = null,
        confirmPassword: String? = null,
    ) {
        binding.register.authTextFieldList.apply {
            when (section) {
                NIM_NISN_SCHOOL -> {
                    if (nimNisnTxtField.text.isNullOrEmpty()) nimNisnTextFieldContainer.showError()
                    else nimNisnTextFieldContainer.hideError()
                    if (kampusSekolahTxtField.text.isNullOrEmpty()) sekolahTextFieldContainer.showError()
                    else sekolahTextFieldContainer.hideError()
                    if (jurusanTxtField.text.isNullOrEmpty()) jurusanTextFieldContainer.showError()
                    else jurusanTextFieldContainer.hideError()
                }

                NAME_NIK_ADDRESS -> {
                    if (namaLengkapTextField.text.isNullOrEmpty()) namaTextFieldContainer.showError()
                    else namaTextFieldContainer.hideError()
                    if (nikTextField.text.isNullOrEmpty()) nikTextFieldContainer.showError()
                    else nikTextFieldContainer.hideError()
                }

                EMAIL_PASSWORD -> {
                    if (email.isNullOrEmpty()) emailTextFieldContainer.showError()
                    else if (!email.isEmailValid())
                        emailTextFieldContainer.showError("Gunakan format email yang benar")
                    else emailTextFieldContainer.hideError()

                    if (password.isNullOrEmpty()) passwordTextFieldContainer.showError()
                    else if (password.length < 8)
                        passwordTextFieldContainer.showError("Minimum Password adalah 8 huruf")
                    else passwordTextFieldContainer.hideError()

                    if (confirmPassword.isNullOrEmpty()) konfirmasiPasswordTextFieldContainer.showError()
                    else if (confirmPassword != password)
                        konfirmasiPasswordTextFieldContainer.showError("Samakan Password dan Konfirmasi Password")
                    else konfirmasiPasswordTextFieldContainer.hideError()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val NIM_NISN_SCHOOL = "section 1"
        private const val NAME_NIK_ADDRESS = "section 2"
        private const val EMAIL_PASSWORD = "section 3"
        private const val CONFIRM_ACCOUNT = "section 4"
    }
}

