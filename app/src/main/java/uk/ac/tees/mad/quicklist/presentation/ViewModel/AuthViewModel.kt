package uk.ac.tees.mad.quicklist.presentation.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    val db: FirebaseFirestore,
    val auth: FirebaseAuth
) : ViewModel() {

    val loading = MutableStateFlow(false)

    private val _currentUser = MutableStateFlow<GetUserInfo?>(null)
    val currentUser: StateFlow<GetUserInfo?> = _currentUser

    init {
        auth.addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.uid?.let {
                fetchCurrentUserData()
            } ?: run {
                _currentUser.value = null
            }
        }
    }


    private fun fetchCurrentUserData() {
        auth.currentUser?.uid?.let { userId ->
            db.collection("user").document(userId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("AuthViewModel", "Error fetching user data", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val data = snapshot.toObject(GetUserInfo::class.java)
                        _currentUser.value = data
                    } else {
                        _currentUser.value = null
                    }
                }
        }
    }

    fun signUp(
        email: String,
        password: String,
        name: String,
        onResult: (String, Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            loading.value = true
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user?.uid

                        if (userId != null) {
                            val userInfo = PostUserInfo(
                                profileImageUrl = "",
                                name = name,
                                email = email,
                                uid = userId,
                                passkey = password,
                                mobNumber = ""
                            )

                            db.collection("user").document(userId).set(userInfo)
                                .addOnSuccessListener {
                                    loading.value = false
                                    onResult("Signup successful", true)
                                }.addOnFailureListener { exception ->
                                    auth.currentUser?.delete()
                                    loading.value = false
                                    onResult("Failed to save user info", false)
                                }
                        } else {
                            loading.value = false
                            onResult("User ID not found", false)
                        }
                    } else {
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthUserCollisionException -> "This email is already registered"
                            is FirebaseAuthWeakPasswordException -> "Password is too weak"
                            else -> task.exception?.localizedMessage ?: "Signup failed"
                        }
                        loading.value = false
                        onResult("mine $errorMessage ", false)
                    }
                }
            } catch (e: Exception) {
                loading.value = false
                onResult("Unexpected error: ${e.localizedMessage}", false)
            }
        }
    }

    fun logIn(
        email: String,
        passkey: String,
        onResult: (String, Boolean) -> Unit,
    ) {
        loading.value = true
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, passkey).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loading.value = false
                        onResult("Login successful", true)
                    } else {
                        val errorMessage = task.exception?.localizedMessage ?: "Login failed"
                        loading.value = false
                        onResult(errorMessage, false)
                    }
                }
            } catch (e: Exception) {
                loading.value = false
                onResult("Error: ${e.localizedMessage}", false)
            }
        }
    }

    fun updateUserInfo(
        name: String? = null,
        mobNumber: String? = null,
        onResult: (String, Boolean) -> Unit
    ) {
        viewModelScope.launch {
            loading.value = true
            try {
                val userId = auth.currentUser?.uid ?: run {
                    loading.value = false
                    onResult("User not authenticated", false)
                    return@launch
                }

                val updates = mutableMapOf<String, Any>()
                name?.let { updates["name"] = it }
                mobNumber?.let { updates["mobNumber"] = it }

                db.collection("user").document(userId).update(updates)
                    .addOnSuccessListener {
                        loading.value = false
                        fetchCurrentUserData()
                        onResult("Profile updated successfully", true)
                    }
                    .addOnFailureListener { e ->
                        loading.value = false
                        onResult("Failed to update profile: ${e.localizedMessage}", false)
                    }
            } catch (e: Exception) {
                loading.value = false
                onResult("Unexpected error: ${e.localizedMessage}", false)
            }
        }
    }

    fun logOut(onResult: (String, Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signOut()
                _currentUser.value = null
                onResult("Logged out successfully", true)
            } catch (e: Exception) {
                onResult("Logout failed: ${e.localizedMessage}", false)
            }
        }
    }
}

data class PostUserInfo(
    val profileImageUrl: String,
    val name: String,
    val email: String,
    val uid: String,
    val mobNumber: String,
    val passkey: String,
)

data class GetUserInfo(
    val profileImageUrl: String = "",
    val name: String = "",
    val email: String = "",
    val uid: String = "",
    val mobNumber: String = "",
    val passkey: String = "",
)