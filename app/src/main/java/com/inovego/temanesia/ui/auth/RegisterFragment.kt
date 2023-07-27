package com.inovego.temanesia.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.R
import com.inovego.temanesia.data.model.UserItem
import com.inovego.temanesia.databinding.FragmentRegisterBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.auth.view.AuthTextInputLinearLayout
import com.inovego.temanesia.utils.CONFIRM_ACCOUNT
import com.inovego.temanesia.utils.EMAIL_PASSWORD_CONFIRM_PASSWORD
import com.inovego.temanesia.utils.NAME_NIK_ADDRESS
import com.inovego.temanesia.utils.NIM_NISN_SCHOOL_JURUSAN
import com.inovego.temanesia.utils.createToast


class RegisterFragment : Fragment() {
    private lateinit var textInputLayout: AuthTextInputLinearLayout
    private lateinit var button: Button

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels {
        ViewModelFactory.getInstance(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        textInputLayout = binding.register.authTextInputLayout
        button = binding.register.authButton
        textInputLayout.getJurusanDropdown(requireContext())
        button.text = "Continue"
        binding.register.authStatusText.apply {
            loginRegisterText.text = "Masuk Disini"
            loginRegisterText.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_register_to_navigation_login)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.toastText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { text ->
                createToast(requireContext(), text)
            }
        }

        viewModel.setSection(NIM_NISN_SCHOOL_JURUSAN)
        viewModel.section.observe(viewLifecycleOwner) { section ->
            when (section) {
                NIM_NISN_SCHOOL_JURUSAN -> {
                    textInputLayout.checkTextInputAndError(section, false)
                    button.setOnClickListener {
                        if (inputTextState(section)) viewModel.setSection(
                            NAME_NIK_ADDRESS
                        )
                    }
                }

                NAME_NIK_ADDRESS -> {
                    textInputLayout.checkTextInputAndError(section, false)
                    button.setOnClickListener {
                        if (inputTextState(section)) viewModel.setSection(
                            EMAIL_PASSWORD_CONFIRM_PASSWORD
                        )
                    }
                }


                EMAIL_PASSWORD_CONFIRM_PASSWORD -> {
                    textInputLayout.checkTextInputAndError(section, false)
                    button.setOnClickListener {
                        val email = textInputLayout.getView().emailTextField.text.toString()
                        val password = textInputLayout.getView().passwordTxtField.text.toString()

                        if (inputTextState(section)) {
                            Firebase.auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        createToast(requireContext(), "Berhasil Membuat Akun")

                                        Firebase.auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener {
                                                viewModel.sendEmailVerification()

                                                val userItemData = setUserData(textInputLayout)
                                                sendDataToDB(userItemData)
                                            }.addOnFailureListener { e ->
                                                createToast(
                                                    requireContext(),
                                                    "Gagal masuk akun : $e"
                                                )
                                            }

                                    } else createToast(
                                        requireContext(),
                                        "Gagal : ${it.exception?.message}"
                                    )
                                }.addOnFailureListener { e ->
                                    createToast(
                                        requireContext(),
                                        "Gagal SignUp : $e"
                                    )
                                }

//                            viewModel.isRegistered.observe(viewLifecycleOwner) { registered ->
//                                if (registered) viewModel.signInFirebase(email, password)
//                            }

//                            viewModel.isSignedIn.observe(viewLifecycleOwner) { signedIn ->
//                                if (signedIn) {
//                                    viewModel.sendEmailVerification()
//
//                                    val userItemData = setUserData(textInputLayout)
//                                    sendDataToDB(userItemData)
//                                }
//                            }

                            viewModel.isUserDataSaved.observe(viewLifecycleOwner) { dataSaved ->
                                if (dataSaved) viewModel.setSection(CONFIRM_ACCOUNT)
                            }
                        }
                    }
                }

                CONFIRM_ACCOUNT -> {
                    textInputLayout.checkTextInputAndError(section, false)
                    button.setOnClickListener {
                        findNavController().navigate(R.id.action_navigation_register_to_navigation_login)
                    }
                }
            }

        }
    }

    private fun inputTextState(section: String): Boolean {
        return textInputLayout.checkTextInputAndError(section)
    }


    private fun setUserData(binding: AuthTextInputLinearLayout): UserItem {
        binding.getView().apply {
            val email = emailTextField.text.toString()
            val alamat = alamatTextField.text.toString()
            val pendidikan = kampusSekolahTxtField.text.toString()
            val jurusan = autoCompleteJurusan.text.toString()
            val nama = namaLengkapTextField.text.toString()
            val nik = nikTextField.text.toString().toLong()
            val nimNisn = nimNisnTxtField.text.toString().toLong()
            val sekolah = kampusSekolahTxtField.text.toString()

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
    }

    private fun sendDataToDB(userData: UserItem) {
        viewModel.uid.observe(viewLifecycleOwner) { uid ->
            viewModel.writeDataOnDB(uid, userData)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

