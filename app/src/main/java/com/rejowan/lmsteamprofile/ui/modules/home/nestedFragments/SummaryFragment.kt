package com.rejowan.lmsteamprofile.ui.modules.home.nestedFragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rejowan.lmsteamprofile.data.remote.response.AllRoundedResponse
import com.rejowan.lmsteamprofile.data.remote.response.Awards
import com.rejowan.lmsteamprofile.data.remote.response.MatchResult
import com.rejowan.lmsteamprofile.data.remote.response.Player
import com.rejowan.lmsteamprofile.data.remote.response.Rankings
import com.rejowan.lmsteamprofile.data.remote.response.SummaryStats
import com.rejowan.lmsteamprofile.data.remote.response.TeamInfo
import com.rejowan.lmsteamprofile.data.remote.response.UpcomingFixture
import com.rejowan.lmsteamprofile.data.remote.response.Video
import com.rejowan.lmsteamprofile.databinding.FragmentSummaryBinding
import com.rejowan.lmsteamprofile.ui.shared.adapter.RecentResultsAdapter
import com.rejowan.lmsteamprofile.ui.shared.adapter.RecentVideoAdapter
import com.rejowan.lmsteamprofile.ui.shared.adapter.SquadPlayerAdapter
import com.rejowan.lmsteamprofile.ui.shared.adapter.TopPlayerAdapter
import com.rejowan.lmsteamprofile.ui.shared.adapter.UpcomingFixturesAdapter
import com.rejowan.lmsteamprofile.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SummaryFragment : Fragment() {

    private var pageSwitcher: PageSwitcher? = null

    interface PageSwitcher {
        fun switchPageInParent(pageIndex: Int)
    }

    private val binding: FragmentSummaryBinding by lazy {
        FragmentSummaryBinding.inflate(layoutInflater)
    }

    private val mainViewModel: MainViewModel by viewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.teamProfileData.observe(viewLifecycleOwner) { profileData ->
            setupTeamInfo(profileData.teamInfo)
            setupSummaryStats(profileData.summaryStats)
            setupRanking(profileData.rankings)

            viewLifecycleOwner.lifecycleScope.launch {
                delay(100)
                setupTopBatsman(profileData.topBatsmen)
                setupTopBowler(profileData.topBowlers)
                setupTopAllRounder(profileData.topAllRounders)
                setupAwards(profileData.awards)
                setupMatchResults(profileData.matchResults)
                setupUpcomingFixtures(profileData.upcomingFixtures)
                setupVideos(profileData.videos)
            }

        }

        mainViewModel.allRounderList.observe(viewLifecycleOwner) { allRounderList ->
            setupSquad(allRounderList)
        }

        clickListeners()

    }

    private fun clickListeners() {

        binding.seeFullBowler.setOnClickListener {
            switchToPage(2)
        }

        binding.seeFullBatsman.setOnClickListener {
            switchToPage(1)
        }

    }


    private fun setupTeamInfo(teamInfo: List<TeamInfo>) {
        binding.description.text = teamInfo[0].teamDescription
    }

    private fun setupSummaryStats(summaryStats: List<SummaryStats>) {
        binding.matches.text = summaryStats[0].gamesPlayed.toString()

        val winRatio = summaryStats[0].winRatio.toString()
        val winRatioArray = winRatio.split(".")
        binding.winRatio.text = winRatioArray[0]
        binding.winRatioPercent.text = ".${winRatioArray[1]} %"

        binding.wins.text = String.format(summaryStats[0].wins.toString())
        binding.loses.text = summaryStats[0].loses.toString()
    }

    private fun setupRanking(rankings: List<Rankings>) {
        binding.cityRank.text = rankings[0].regionalRank.toString()
        binding.nationalRank.text = rankings[0].countryRank.toString()
        binding.worldRank.text = rankings[0].worldRank.toString()

        val form = rankings[0].form
        val formArray = form.split(" ")
        val spannableString = SpannableStringBuilder()
        for (formItem in formArray) {
            val start = spannableString.length
            spannableString.append(formItem)

            val color = if (formItem == "W") Color.GREEN else Color.RED

            spannableString.setSpan(
                ForegroundColorSpan(color), start, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannableString.append(" ")

        }
        binding.form.text = spannableString


    }


    private fun setupTopBatsman(topBatsmen: List<Player>) {
        val adapter = TopPlayerAdapter(topBatsmen.take(6))
        binding.recyclerViewTopBatsmen.adapter = adapter
        binding.recyclerViewTopBatsmen.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
    }

    private fun setupTopBowler(topBowlers: List<Player>) {
        val adapter = TopPlayerAdapter(topBowlers.take(6))
        binding.recyclerViewTopBowlers.adapter = adapter
        binding.recyclerViewTopBowlers.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
    }

    private fun setupTopAllRounder(topAllRounders: List<Player>) {
        val adapter = TopPlayerAdapter(topAllRounders.take(6))
        binding.recyclerViewTopAllRounders.adapter = adapter
        binding.recyclerViewTopAllRounders.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
    }


    private fun setupSquad(allRounderList: MutableList<AllRoundedResponse>) {
        val adapter = SquadPlayerAdapter(allRounderList.take(3))
        binding.recyclerViewSquad.adapter = adapter
        binding.recyclerViewSquad.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )

        binding.scrollSquad.setOnClickListener {
            val last = adapter.itemCount - 1
            binding.recyclerViewSquad.smoothScrollToPosition(last)

        }

        binding.recyclerViewSquad.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = adapter.itemCount

                if (lastVisiblePosition == totalItemCount - 1) {
                    binding.scrollSquad.visibility = View.GONE
                } else {
                    binding.scrollSquad.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun setupAwards(awards: List<Awards>) {
        binding.champions.text = awards[0].champion.toString()
        binding.runersUp.text = awards[0].runnersUp.toString()
    }

    private fun setupMatchResults(matchResults: List<MatchResult>) {
        val adapter = RecentResultsAdapter(matchResults.take(5))
        binding.recyclerViewRecentResults.adapter = adapter
        binding.recyclerViewRecentResults.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )

    }

    private fun setupUpcomingFixtures(upcomingFixtures: List<UpcomingFixture>) {
        val adapter = UpcomingFixturesAdapter(upcomingFixtures.take(5))
        binding.recyclerViewUpcomingFixtures.adapter = adapter
        binding.recyclerViewUpcomingFixtures.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
    }

    private fun setupVideos(videos: List<Video>) {
        val adapter = RecentVideoAdapter(videos.take(3))
        binding.recyclerViewVideos.adapter = adapter
        binding.recyclerViewVideos.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is PageSwitcher) {
            pageSwitcher = parentFragment as PageSwitcher
        } else {
            throw IllegalStateException("Parent fragment must implement PageSwitcher")
        }
    }

    override fun onDetach() {
        super.onDetach()
        pageSwitcher = null
    }

    private fun switchToPage(pageIndex: Int) {
        pageSwitcher?.switchPageInParent(pageIndex)
    }


}