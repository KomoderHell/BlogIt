package com.komodorhell.blogit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterLoginActivity : FragmentActivity() {

    private val NO_OF_PAGES = 2
    lateinit var pickedImageUri: Uri


    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = Firebase.auth

        if (mAuth.currentUser != null) {
            nextPage()
        }


        val registerLoginViewPagerAdapter = RegisterLoginViewPagerAdapter(this)
        viewPagerRegisterLogin.adapter = registerLoginViewPagerAdapter

    }

    fun nextPage() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun createUser(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        imageUri: Uri
    ) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "signup complete", Toast.LENGTH_SHORT).show()

                    pickedImageUri = imageUri
                    updateUserInfo()
                } else {
//                    Toast.makeText(
//                        applicationContext,
//                        "signup failed " + task.exception.toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

            }

    }

    private fun updateUserInfo() {
        val currentUser = mAuth.currentUser
        val mStorage = FirebaseStorage.getInstance().reference.child("users_photos")
        val imageFilePath = mStorage.child(pickedImageUri.lastPathSegment!!)
        val uploadTask = imageFilePath.putFile(pickedImageUri)
        uploadTask.addOnSuccessListener {

            Toast.makeText(applicationContext, "success", Toast.LENGTH_SHORT).show()


            imageFilePath.downloadUrl.addOnSuccessListener {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(editTextViewFirstName.text.toString() + " " + editTextViewLastName.text.toString())
                    .setPhotoUri(it)
                    .build()

                currentUser?.updateProfile(profileUpdate)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(applicationContext, "complete", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

            }.addOnFailureListener {
//                Toast.makeText(applicationContext,"failure " + it.toString()    , Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(applicationContext, "failure " + it.toString(), Toast.LENGTH_SHORT)
                .show()
        }

        nextPage()
        finish()

    }

    fun loginUser(username: String, password: String) {
        mAuth.signInWithEmailAndPassword(username, password).addOnSuccessListener {
            Toast.makeText(this, "Logging in", Toast.LENGTH_SHORT).show()
            nextPage()
        }.addOnFailureListener {
            Toast.makeText(this, "" + it.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    fun verifyInputInfo(
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ): Boolean {
        if (firstName == "" || lastName == "" || email == "" || password == "") {
            Toast.makeText(applicationContext, "please fill all the details!!", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true;
    }

    fun verifyInput(username: String, password: String): Boolean {
        if (username == "" || password == "") {
            Toast.makeText(applicationContext, "please enter required fields", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true

    }


    private inner class RegisterLoginViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return NO_OF_PAGES
        }

        override fun createFragment(position: Int): Fragment {
            if (position == 1) {
                return LoginFragment()
            } else {
                return RegisterFragment()
            }
        }
    }

}