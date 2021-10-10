package kz.gvsx.tou.ui.article

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.gvsx.tou.R
import kz.gvsx.tou.databinding.FragmentArticleBinding

@AndroidEntryPoint
class ArticleFragment : Fragment(R.layout.fragment_article) {

    private val binding: FragmentArticleBinding by viewBinding()
    private val viewModel: ArticleViewModel by viewModels()
    private val args: ArticleFragmentArgs by navArgs()

    private var imageViewer: StfalconImageViewer<String>? = null
    private val imagesAdapter = ImagesAdapter { position, view ->
        imageViewer = StfalconImageViewer.Builder(context, viewModel.images.value, ::loadImage)
            .withStartPosition(position)
            .withTransitionFrom(view as ImageView)
            .withImageChangeListener(::onImageChanged)
            .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.title = args.title
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        viewModel.fetchArticle(args.articleLink)

        binding.recyclerView.apply {
            adapter = imagesAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        viewModel.images.observe(viewLifecycleOwner) { images ->
            imagesAdapter.submitList(images)
        }

        viewModel.text.observe(viewLifecycleOwner) { text ->
            binding.textView.text = text
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        imageViewer = null
    }

    private fun loadImage(view: ImageView, image: String) {
        view.load(image) {
            crossfade(true)
        }
    }

    private fun onImageChanged(position: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.recyclerView.scrollToPosition(position)
            delay(20)
            val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(position)
            if (viewHolder is ImagesAdapter.ViewHolder)
                imageViewer?.updateTransitionImage(viewHolder.binding.imageThumb)
        }
    }
}