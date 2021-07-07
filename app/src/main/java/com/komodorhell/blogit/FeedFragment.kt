package com.komodorhell.blogit

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.layout_create_post_dialog.*
import java.sql.Time
import java.sql.Timestamp

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeedFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase: FirebaseDatabase
    lateinit var currentUser: FirebaseUser
    lateinit var addPostDialog: Dialog
    lateinit var posts: ArrayList<PostModel>

    private val request_code = 2;
    lateinit var postImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        posts = arrayListOf()
        val manager = LinearLayoutManager(context)
        val adapter = FeedAdapter(posts)

        mAuth = Firebase.auth
        currentUser = mAuth.currentUser
        mDatabase = FirebaseDatabase.getInstance()

        //set up feed recycler view
        val ref = mDatabase.reference.child("Posts")
        val postListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var newPost: PostModel
//                Toast.makeText(context,snapshot.value.toString(),Toast.LENGTH_SHORT).show()
                posts.clear()
                if (snapshot.exists()) {
                    for (data: DataSnapshot in snapshot.children) {
                        newPost = data.getValue(PostModel::class.java)!!
                        Toast.makeText(context, data.getValue().toString(), Toast.LENGTH_SHORT)
                            .show()
                        posts.add(newPost)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        }
        ref.addValueEventListener(postListener)
        recyclerViewFeed.layoutManager = manager
        recyclerViewFeed.adapter = adapter



        addPostDialog = Dialog(requireContext())

        floatingCreatePost.setOnClickListener {
            addPostDialog.window?.setContentView(R.layout.layout_create_post_dialog)
            addPostDialog.window?.setLayout(
                Toolbar.LayoutParams.MATCH_PARENT,
                Toolbar.LayoutParams.MATCH_PARENT
            )
            addPostDialog.show()

            Glide.with(requireContext()).load(currentUser.photoUrl)
                .into(addPostDialog.imageViewProfilePic)

            addPostDialog.floatingSave.setOnClickListener {
                val title = addPostDialog.editTextViewPostTitle.text.toString()
                val desc = addPostDialog.editTextViewDescription.text.toString()
                if (title == "" || desc == "") {
                    Toast.makeText(context, "please enter required details", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val mStorage = FirebaseStorage.getInstance().reference.child("post_photos")
                    val imageFilePath = mStorage.child(postImageUri.lastPathSegment!!)
                    val uploadTask = imageFilePath.putFile(postImageUri)
                    uploadTask.addOnSuccessListener {
                        imageFilePath.downloadUrl.addOnSuccessListener {
                            val img = it.toString()
                            val userid = currentUser.uid.toString()
                            val userimg = currentUser.photoUrl.toString()
                            val timestamp = System.currentTimeMillis()
                            createPost(title, desc, img, userid, userimg, timestamp)
                        }
                    }
                }
                addPostDialog.hide()
            }

            addPostDialog.imageViewAddImage.setOnClickListener {
                openGallery()
            }


        }


    }

    private fun createPost(
        title: String,
        desc: String,
        img: String,
        userid: String,
        userimg: String,
        timestamp: Long
    ) {
        val myref = mDatabase.getReference("Posts").push()

        val key = myref.key

        val postModel = PostModel(key!!, title, desc, img, userid, userimg, timestamp)
        myref.setValue(postModel).addOnSuccessListener {
            Toast.makeText(context, "post Added", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.setType("image/*")
        startActivityForResult(galleryIntent, request_code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == request_code) {
            postImageUri = data?.data!!
            addPostDialog.imageViewAddImage.setImageURI(postImageUri)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FeedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FeedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}