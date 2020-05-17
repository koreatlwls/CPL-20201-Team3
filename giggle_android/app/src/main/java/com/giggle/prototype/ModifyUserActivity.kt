package com.giggle.prototype

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_modify_user.*
import java.io.IOException

class ModifyUserActivity : AppCompatActivity() {
    private val user = FirebaseAuth.getInstance().currentUser
    private val storage = FirebaseStorage.getInstance()

    private val CHOOSING_IMAGE_REQUEST = 0
    private var fileUri: Uri? = null
    private var bitmap: Bitmap? = null
    private var imageReference: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_user)



        // Toast.makeText(this, "toast", Toast.LENGTH_LONG).show()

        // get user's info
        val name = user?.displayName
        val email = user?.email
        val uid = user?.uid

        // TODO: see docs/auth/android/manage-users, split into two parts, construct linearlayout

        // initialize fields
        NameText.setText(name)
        EmailText.setText(email)
        // PasswordText.setText("")


        PicButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSING_IMAGE_REQUEST)
        }


        ModifyUserOK.setOnClickListener {
            // get fields
            val nameInput = NameText.text.toString()
            val emailInput = EmailText.text.toString()
            val passwordInput = PasswordText.text.toString()

            // updateProfile - name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(nameInput)
                .build()
            user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "updateProfile에서 문제가 발생했습니다.", Toast.LENGTH_LONG).show()
                    }
                }

            // updateEmail
            user?.updateEmail(emailInput)
                ?.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "updateEmail에서 문제가 발생했습니다.", Toast.LENGTH_LONG).show()
                    }
                }


            // if password is not empty, update password too
            if (passwordInput.isNotEmpty()) {
                user?.updatePassword(passwordInput)
                    ?.addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Toast.makeText(this, "updatePassword에서 문제가 발생했습니다.", Toast.LENGTH_LONG).show()
                        }
                    }
            }

            // update profile pic
            uploadFile(uid.toString())

            // completed
            Toast.makeText(this, "회원정보 수정이 완료되었습니다.", Toast.LENGTH_LONG).show()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (bitmap != null) {
            bitmap!!.recycle()
        }

        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
                // imgFile.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadFile(uid: String) {
        if (fileUri != null) {
            val storageRef = storage.reference
            val imageRef = storageRef.child("profile_image/${uid}.png")
            imageRef.putFile(fileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(this, "File Uploaded ", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
        } else {
            // Profile pic not selected
            // skip profile pic update

            // Toast.makeText(this, "No File!", Toast.LENGTH_LONG).show()
        }
    }

}
