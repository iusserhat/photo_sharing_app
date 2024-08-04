package com.example.fotorafpayamuygulamas.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fotorafpayamuygulamas.R
import com.example.fotorafpayamuygulamas.adapter.PostAdapter
import com.example.fotorafpayamuygulamas.databinding.FragmentFeedBinding
import com.example.fotorafpayamuygulamas.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var popup: PopupMenu
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val postList: ArrayList<Post> = arrayListOf()
    private var adapter: PostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener { floatingButtonTiklandi(it) }

        popup = PopupMenu(requireContext(), binding.floatingActionButton).apply {
            menuInflater.inflate(R.menu.my_popup_menu, menu)
            setOnMenuItemClickListener(this@FeedFragment)
        }

        firestoreVeriAl()
        adapter = PostAdapter(postList)
        binding.feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feedRecyclerView.adapter = adapter
    }

    private fun firestoreVeriAl() {
        db.collection("Posts").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                value?.let {
                    if (it.documents.isNotEmpty()) {
                        postList.clear()
                        for (document in it.documents) {
                            val comment = document.getString("comment") ?: ""
                            val email = document.getString("userEmail") ?: ""
                            val downloadUrl = document.getString("downloadUrl") ?: ""
                            val post = Post(email , comment, downloadUrl)
                            postList.add(post)

                        }
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun floatingButtonTiklandi(view: View) {
        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.yuklemeItem -> {
                val action = FeedFragmentDirections.actionFeedFragmentToYuklemeFragment()
                Navigation.findNavController(requireView()).navigate(action)
                return true
            }
            R.id.cikisItem -> {
                auth.signOut()
                val action = FeedFragmentDirections.actionFeedFragmentToKullaniciFragment()
                Navigation.findNavController(requireView()).navigate(action)
                return true
            }
        }
        return false
    }
}
