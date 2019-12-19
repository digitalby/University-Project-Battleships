package me.digitalby.lr5

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN_GOOGLE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        if(auth.currentUser != null)
            goToLobby()
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        signInWithGoogleButton.setOnClickListener { view ->
            onClickSignUpGoogle(view)
        }
    }

    private fun goToLobby() {
        val currentUser = auth.currentUser
        if(currentUser == null) {
            Toast.makeText(applicationContext,"You're not signed in.",Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uid", currentUser.uid)
        startActivity(intent)
        finish()
    }

    fun onClickSignUpEmail(view: View) {
        val email = loginEmailEditText.text.toString()
        val password = loginPasswordEditText.text.toString()
        if(!verifyEmailPassword(email, password))
            return
        var fail = false
        auth.createUserWithEmailAndPassword(email, password).addOnFailureListener { e ->
            Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            fail = true
        }.addOnCompleteListener { task ->
            if(fail)
                return@addOnCompleteListener
            if(!task.isSuccessful) {
                Toast.makeText(applicationContext,"Error: ${task.result.toString()}",Toast.LENGTH_LONG).show()
                return@addOnCompleteListener
            }
            goToLobby()
        }
    }

    fun onClickSignUpGoogle(view: View) {
        val signInGoogleIntent = googleSignInClient.signInIntent
        startActivityForResult(signInGoogleIntent, RC_SIGN_IN_GOOGLE)
    }

    fun onClickSignUpFacebook(view: View) {
        Toast.makeText(applicationContext,"Facebook",Toast.LENGTH_LONG).show()
    }

    fun onClickSignUpGithub(view: View) {
        Toast.makeText(applicationContext,"Github",Toast.LENGTH_LONG).show()
    }

    fun onClickSignInEmail(view: View) {
        val email = loginEmailEditText.text.toString()
        val password = loginPasswordEditText.text.toString()
        if (!verifyEmailPassword(email, password))
            return
        var fail = false
        auth.signInWithEmailAndPassword(email, password).addOnFailureListener { e ->
            Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            fail = true
        }.addOnCompleteListener { task ->
            if(fail)
                return@addOnCompleteListener
            if (!task.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    "Error: ${task.result.toString()}",
                    Toast.LENGTH_LONG
                ).show()
                return@addOnCompleteListener
            }
            goToLobby()
        }
    }

    fun googleToFirebase(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                goToLobby()
            } else {
                Toast.makeText(applicationContext, "Google Sign-in Error.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            RC_SIGN_IN_GOOGLE -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if(account != null)
                        googleToFirebase(account)
                } catch (e: ApiException) {
                    Toast.makeText(applicationContext, "Google Sign-in failed: ${e.message}", Toast.LENGTH_LONG).show()
                    return
                }
            }
        }
    }

    fun verifyEmailPassword(email: String, password: String): Boolean {
        if(email.isEmpty()) {
            Toast.makeText(applicationContext,"You didn't provide an email.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(password.isEmpty()) {
            Toast.makeText(applicationContext,"You didn't provide a password.",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
