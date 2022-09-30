package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {
    private val viewModel: VoterInfoViewModel by lazy {

        val application = requireNotNull(this.activity).application
        val viewModelFactory = VoterInfoViewModelFactory(application)
        ViewModelProvider(this, viewModelFactory)
            .get(VoterInfoViewModel::class.java)
    }
    private val args: VoterInfoFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values
        val binding: FragmentVoterInfoBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_voter_info,
            container,
            false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val election = args.argElection

        viewModel.getElection(election.id)
        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */

        if (election.division.state.isEmpty()) {
            viewModel.getVoterInfo(args.argElection.id, args.argElection.division.country)
        } else {
            viewModel.getVoterInfo(args.argElection.id, "${args.argElection.division.country} - ${args.argElection.division.state}")
        }

        viewModel.Url.observe(viewLifecycleOwner, Observer {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            startActivity(intent)
        })

        //TODO: cont'd Handle save button clicks
        return binding.root


    }

    //TODO: Create method to load URL intents

}