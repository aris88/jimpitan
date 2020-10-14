package com.example.jimpitan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var providers : List<AuthUI.IdpConfig>

    val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //inisialisasi
       providers = Arrays.asList<AuthUI.IdpConfig>(
           AuthUI.IdpConfig.EmailBuilder().build(),
           AuthUI.IdpConfig.PhoneBuilder().build(),
           AuthUI.IdpConfig.GoogleBuilder().build()
       )

        showSignInOptions()

        btnLogout.setOnClickListener{
            AuthUI.getInstance().signOut(this)
                .addOnCompleteListener {
                    btnLogout.isEnabled = false
                    showSignInOptions()
                }
                .addOnFailureListener {
                    e -> Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, "phone: "+user!!.phoneNumber+" /email: "+ user.email, Toast.LENGTH_SHORT).show()
                btnLogout.isEnabled = true
            }
            else{
                Toast.makeText(this, ""+response!!.error!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSignInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.MyTheme)
            .build(), RC_SIGN_IN)
    }
}