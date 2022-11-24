package com.example.mysportsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mysportsapp.databinding.FragmentFirstBinding
import kotlinx.coroutines.*
import okhttp3.OkHttpClient

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */



class FirstFragment : Fragment() {

    val model: SearchViewModel by viewModels<SearchViewModel>()

    // First Fragment state, behaviors and data
    class SearchViewModel(val repo: TeamRepository = TeamRepository()): ViewModel() {
        // Observable array of teams
        private val teams: MutableLiveData<List<Team>> by lazy {
            MutableLiveData<List<Team>>()
        }
        fun getTeams(): LiveData<List<Team>> {
            return teams
        }
        // Job maintains a debounce on incoming
        var searchJob: Job? = null
        fun searchTeams(term: String) {
            searchJob?.cancel()
            // pop to background thread
            searchJob = viewModelScope.launch(Dispatchers.IO) {
                // Debounce delay for 500 milliseconds
                delay(500)
                // call api for teams
                val teams = repo.searchTeams(term)
                viewModelScope.launch(Dispatchers.Main) {
                    // update teams on the main thread
                    this@SearchViewModel.teams.value = teams
                }
            }
        }
    }

    // Create Search Adapter with onCLick that opens new fragment
    val searchAdapter = SearchTeamRecyclerViewAdapter( onClick = {
        onSelectTeam(it)
    })

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)


        return binding.root

    }

    // Open new Fragment with the Team bundle
    fun onSelectTeam(team: Team) {
        findNavController()
            .navigate(R.id.action_FirstFragment_to_SecondFragment, bundleOf("team" to team))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set LayoutManger on RecyclerView
        binding.SearchResults.layoutManager = LinearLayoutManager(context)
        // Set adapter to recyclerview
        binding.SearchResults.adapter = searchAdapter


        // Observe ViewModel.teams for any changes and update and update the search adapter
        model.getTeams().observe(viewLifecycleOwner, Observer<List<Team>>{ teams ->
            searchAdapter.teams = teams
            searchAdapter.notifyDataSetChanged()
        })
        // Listen to text input changes
        binding.SearchBar.setOnQueryTextListener(object: OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                model.searchTeams(p0 ?: "")
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                model.searchTeams(p0 ?: "")
                println("Call Search Teams")

                return true
            }

        })
        binding.SearchButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                model.searchTeams(binding.SearchBar.query.toString() ?: "")
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

