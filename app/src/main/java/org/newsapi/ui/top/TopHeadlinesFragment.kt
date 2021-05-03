package org.newsapi.ui.top

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsapi.api.model.Article
import org.newsapi.CATEGORY_GENERAL
import org.newsapi.CATEGORY_LIST
import org.newsapi.COUNTRY_IN
import org.newsapi.R
import org.newsapi.databinding.FragmentTopHeadlinesBinding
import org.newsapi.ui.NewsRecyclerAdapter

class TopHeadlinesFragment : Fragment(), NewsRecyclerAdapter.ArticleClickListener,
    CategoryAdapter.CategoryClickListener {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var newsRecyclerAdapter: NewsRecyclerAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var binding: FragmentTopHeadlinesBinding? = null
    private var progressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopHeadlinesBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        setUpRecyclerView()

        observeForData(CATEGORY_GENERAL)

        return binding?.root
    }

    private fun observeForData(category: String) {
        progressBar?.visibility = View.VISIBLE
        homeViewModel.fetchTopHeadlines(COUNTRY_IN, category)
        homeViewModel.articleLiveData.observe(viewLifecycleOwner, {
            newsRecyclerAdapter.submitList(it)
            progressBar?.visibility = View.GONE
        })
    }

    private fun setUpRecyclerView() {
        newsRecyclerAdapter = NewsRecyclerAdapter(this)
        binding?.homeRecyclerview?.layoutManager = LinearLayoutManager(context)
        binding?.homeRecyclerview?.adapter = newsRecyclerAdapter

        categoryAdapter = CategoryAdapter(this)
        binding?.categoryRecyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.categoryRecyclerView?.adapter = categoryAdapter
        categoryAdapter.submitList(CATEGORY_LIST)

        progressBar = binding?.progressBar
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onArticleClicked(article: Article) {
        homeViewModel.setSelectedArticle(article)
        findNavController().navigate(R.id.action_navigation_top_headlines_to_detailFragment)
    }

    override fun onCategoryClicked(category: String) {
        observeForData(category)
    }
}