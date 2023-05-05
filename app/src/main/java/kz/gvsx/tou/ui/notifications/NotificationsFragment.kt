package kz.gvsx.tou.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kz.gvsx.tou.R
import kz.gvsx.tou.databinding.FragmentNotificationsBinding

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private val binding: FragmentNotificationsBinding by viewBinding()
    private val viewModel: NotificationsViewModel by viewModels()

    private val notificationsAdapter = NotificationsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            swipeRefresh.isRefreshing = true
            swipeRefresh.setOnRefreshListener {
                viewModel.fetchNotifications()
            }
            recyclerView.adapter = notificationsAdapter
        }

        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            notificationsAdapter.submitList(notifications)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
    }
}
