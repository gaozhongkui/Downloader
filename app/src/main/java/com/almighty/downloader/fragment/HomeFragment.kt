package com.almighty.downloader.fragment

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ContextMenu
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.almighty.downloader.AppTheme
import com.almighty.downloader.R
import com.almighty.downloader.animation.AnimationUtils
import com.almighty.downloader.browser.BrowserContract
import com.almighty.downloader.browser.BrowserPresenter
import com.almighty.downloader.browser.BrowserStateAdapter
import com.almighty.downloader.browser.PartialBrowserViewState
import com.almighty.downloader.browser.bookmark.BookmarkRecyclerViewAdapter
import com.almighty.downloader.browser.color.ColorAnimator
import com.almighty.downloader.browser.di.injector
import com.almighty.downloader.browser.image.ImageLoader
import com.almighty.downloader.browser.keys.KeyEventAdapter
import com.almighty.downloader.browser.menu.MenuItemAdapter
import com.almighty.downloader.browser.search.IntentExtractor
import com.almighty.downloader.browser.search.SearchListener
import com.almighty.downloader.browser.search.StyleRemovingTextWatcher
import com.almighty.downloader.browser.tab.DesktopTabRecyclerViewAdapter
import com.almighty.downloader.browser.tab.DrawerTabRecyclerViewAdapter
import com.almighty.downloader.browser.tab.TabPager
import com.almighty.downloader.browser.tab.TabViewHolder
import com.almighty.downloader.browser.tab.TabViewState
import com.almighty.downloader.browser.ui.BookmarkConfiguration
import com.almighty.downloader.browser.ui.TabConfiguration
import com.almighty.downloader.browser.ui.UiConfiguration
import com.almighty.downloader.browser.view.targetUrl.LongPress
import com.almighty.downloader.constant.HTTP
import com.almighty.downloader.database.Bookmark
import com.almighty.downloader.database.HistoryEntry
import com.almighty.downloader.database.SearchSuggestion
import com.almighty.downloader.database.WebPage
import com.almighty.downloader.database.downloads.DownloadEntry
import com.almighty.downloader.databinding.FragmentHomeLayoutBinding
import com.almighty.downloader.dialog.BrowserDialog
import com.almighty.downloader.dialog.DialogItem
import com.almighty.downloader.dialog.LightningDialogBuilder
import com.almighty.downloader.extensions.color
import com.almighty.downloader.extensions.drawable
import com.almighty.downloader.extensions.resizeAndShow
import com.almighty.downloader.extensions.takeIfInstance
import com.almighty.downloader.extensions.tint
import com.almighty.downloader.preference.UserPreferences
import com.almighty.downloader.search.SuggestionsAdapter
import com.almighty.downloader.ssl.createSslDrawableForState
import com.almighty.downloader.utils.ProxyUtils
import com.almighty.downloader.utils.value
import javax.inject.Inject

class HomeFragment : Fragment() {
    private lateinit var tabsAdapter: ListAdapter<TabViewState, TabViewHolder>
    private lateinit var bookmarksAdapter: BookmarkRecyclerViewAdapter

    @Inject
    internal lateinit var userPreferences: UserPreferences

    private var menuItemShare: MenuItem? = null
    private var menuItemCopyLink: MenuItem? = null
    private var menuItemAddToHome: MenuItem? = null
    private var menuItemAddBookmark: MenuItem? = null

    private val defaultColor by lazy { requireContext().color(R.color.primary_color) }
    private val backgroundDrawable by lazy { ColorDrawable(defaultColor) }

    private var customView: View? = null


    @Suppress("ConvertLambdaToReference")
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { presenter.onFileChooserResult(it) }

    @Inject
    internal lateinit var imageLoader: ImageLoader

    @Inject
    internal lateinit var keyEventAdapter: KeyEventAdapter

    @Inject
    internal lateinit var menuItemAdapter: MenuItemAdapter

    @Inject
    internal lateinit var inputMethodManager: InputMethodManager

    @Inject
    internal lateinit var presenter: BrowserPresenter

    @Inject
    internal lateinit var tabPager: TabPager

    @Inject
    internal lateinit var intentExtractor: IntentExtractor

    @Inject
    internal lateinit var lightningDialogBuilder: LightningDialogBuilder

    @Inject
    internal lateinit var uiConfiguration: UiConfiguration

    @Inject
    internal lateinit var proxyUtils: ProxyUtils


    private lateinit var binding: FragmentHomeLayoutBinding


    /**
     * True if the activity is operating in incognito mode, false otherwise.
     */
    fun isIncognito(): Boolean = false

    /**
     * Provide the menu used by the browser instance.
     */
    @MenuRes
    fun menu(): Int = R.menu.main

    /**
     * Provide the home icon used by the browser instance.
     */
    @DrawableRes
    fun homeIcon(): Int = R.drawable.ic_action_home

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDetached()
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewHidden()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuItemShare = menu.findItem(R.id.action_share)
        menuItemCopyLink = menu.findItem(R.id.action_copy)
        menuItemAddToHome = menu.findItem(R.id.action_add_to_homescreen)
        menuItemAddBookmark = menu.findItem(R.id.action_add_bookmark)
        super.onCreateContextMenu(menu, v, menuInfo)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.browser2ComponentBuilder().activity(requireActivity()).browserFrame(binding.contentFrame).toolbarRoot(binding.uiLayout)
            .toolbar(binding.toolbarLayout).initialIntent(requireActivity().intent).incognitoMode(isIncognito()).build().inject(this)

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {

            override fun onDrawerOpened(drawerView: View) {
                if (drawerView == binding.tabDrawer) {
                    presenter.onTabDrawerMoved(isOpen = true)
                } else if (drawerView == binding.bookmarkDrawer) {
                    presenter.onBookmarkDrawerMoved(isOpen = true)
                }
            }

            override fun onDrawerClosed(drawerView: View) {
                if (drawerView == binding.tabDrawer) {
                    presenter.onTabDrawerMoved(isOpen = false)
                } else if (drawerView == binding.bookmarkDrawer) {
                    presenter.onBookmarkDrawerMoved(isOpen = false)
                }
            }
        })

        binding.bookmarkDrawer.layoutParams = (binding.bookmarkDrawer.layoutParams as DrawerLayout.LayoutParams).apply {
            gravity = when (uiConfiguration.bookmarkConfiguration) {
                BookmarkConfiguration.LEFT -> Gravity.START
                BookmarkConfiguration.RIGHT -> Gravity.END
            }
        }

        binding.tabDrawer.layoutParams = (binding.tabDrawer.layoutParams as DrawerLayout.LayoutParams).apply {
            gravity = when (uiConfiguration.bookmarkConfiguration) {
                BookmarkConfiguration.LEFT -> Gravity.END
                BookmarkConfiguration.RIGHT -> Gravity.START
            }
        }

        binding.homeImageView.isVisible = uiConfiguration.tabConfiguration == TabConfiguration.DESKTOP || isIncognito()
        binding.homeImageView.setImageResource(homeIcon())
        binding.tabCountView.isVisible = uiConfiguration.tabConfiguration == TabConfiguration.DRAWER && !isIncognito()

        if (uiConfiguration.tabConfiguration == TabConfiguration.DESKTOP) {
            binding.drawerLayout.setDrawerLockMode(
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED, binding.tabDrawer
            )
        }

        if (uiConfiguration.tabConfiguration == TabConfiguration.DRAWER) {
            tabsAdapter = DrawerTabRecyclerViewAdapter(
                onClick = presenter::onTabClick, onCloseClick = presenter::onTabClose, onLongClick = presenter::onTabLongClick
            )
            binding.drawerTabsList.isVisible = true
            binding.drawerTabsList.adapter = tabsAdapter
            binding.drawerTabsList.layoutManager = LinearLayoutManager(requireContext())
            binding.desktopTabsList.isVisible = false
        } else {
            tabsAdapter = DesktopTabRecyclerViewAdapter(
                context = requireContext(), onClick = presenter::onTabClick, onCloseClick = presenter::onTabClose, onLongClick = presenter::onTabLongClick
            )
            binding.desktopTabsList.isVisible = true
            binding.desktopTabsList.adapter = tabsAdapter
            binding.desktopTabsList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            binding.desktopTabsList.itemAnimator?.takeIfInstance<SimpleItemAnimator>()?.supportsChangeAnimations = false
            binding.drawerTabsList.isVisible = false
        }

        bookmarksAdapter = BookmarkRecyclerViewAdapter(
            onClick = presenter::onBookmarkClick, onLongClick = presenter::onBookmarkLongClick, imageLoader = imageLoader
        )
        binding.bookmarkListView.adapter = bookmarksAdapter
        binding.bookmarkListView.layoutManager = LinearLayoutManager(requireContext())

        presenter.onViewAttached(BrowserStateAdapter(this))

        val suggestionsAdapter = SuggestionsAdapter(requireContext(), isIncognito = isIncognito()).apply {
            onSuggestionInsertClick = {
                if (it is SearchSuggestion) {
                    binding.search.setText(it.title)
                    binding.search.setSelection(it.title.length)
                } else {
                    binding.search.setText(it.url)
                    binding.search.setSelection(it.url.length)
                }
            }
        }
        binding.search.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            binding.search.clearFocus()
            presenter.onSearchSuggestionClicked(suggestionsAdapter.getItem(position) as WebPage)
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }
        binding.search.setAdapter(suggestionsAdapter)
        val searchListener = SearchListener(
            onConfirm = { presenter.onSearch(binding.search.text.toString()) }, inputMethodManager = inputMethodManager
        )
        binding.search.setOnEditorActionListener(searchListener)
        binding.search.setOnKeyListener(searchListener)
        binding.search.addTextChangedListener(StyleRemovingTextWatcher())
        binding.search.setOnFocusChangeListener { _, hasFocus ->
            presenter.onSearchFocusChanged(hasFocus)
            binding.search.selectAll()
        }

        binding.findPrevious.setOnClickListener { presenter.onFindPrevious() }
        binding.findNext.setOnClickListener { presenter.onFindNext() }
        binding.findQuit.setOnClickListener { presenter.onFindDismiss() }

        binding.homeButton.setOnClickListener { presenter.onTabCountViewClick() }
        binding.actionBack.setOnClickListener { presenter.onBackClick() }
        binding.actionForward.setOnClickListener { presenter.onForwardClick() }
        binding.actionHome.setOnClickListener { presenter.onHomeClick() }
        binding.newTabButton.setOnClickListener { presenter.onNewTabClick() }
        binding.newTabButton.setOnLongClickListener {
            presenter.onNewTabLongClick()
            true
        }
        binding.searchRefresh.setOnClickListener { presenter.onRefreshOrStopClick() }
        binding.actionAddBookmark.setOnClickListener { presenter.onStarClick() }
        binding.actionPageTools.setOnClickListener { presenter.onToolsClick() }
        binding.tabHeaderButton.setOnClickListener { presenter.onTabMenuClick() }
        binding.bookmarkBackButton.setOnClickListener { presenter.onBookmarkMenuClick() }
        binding.searchSslStatus.setOnClickListener { presenter.onSslIconClick() }

        tabPager.longPressListener = presenter::onPageLongPress

        /*   onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
               override fun handleOnBackPressed() {
                   presenter.onNavigateBack()
               }

           })*/
    }


    /**
     * @see BrowserContract.View.renderState
     */
    fun renderState(viewState: PartialBrowserViewState) {
        viewState.isBackEnabled?.let { binding.actionBack.isEnabled = it }
        viewState.isForwardEnabled?.let { binding.actionForward.isEnabled = it }
        viewState.displayUrl?.let(binding.search::setText)
        viewState.sslState?.let {
            binding.searchSslStatus.setImageDrawable(requireContext().createSslDrawableForState(it))
            binding.searchSslStatus.updateVisibilityForDrawable()
        }
        viewState.enableFullMenu?.let {
            menuItemShare?.isVisible = it
            menuItemCopyLink?.isVisible = it
            menuItemAddToHome?.isVisible = it
            menuItemAddBookmark?.isVisible = it
        }
        viewState.themeColor?.value()?.let(::animateColorChange)
        viewState.progress?.let { binding.progressView.progress = it }
        viewState.isRefresh?.let {
            binding.searchRefresh.setImageResource(
                if (it) {
                    R.drawable.ic_action_refresh
                } else {
                    R.drawable.ic_action_delete
                }
            )
        }
        viewState.bookmarks?.let(bookmarksAdapter::submitList)
        viewState.isBookmarked?.let { binding.actionAddBookmark.isSelected = it }
        viewState.isBookmarkEnabled?.let { binding.actionAddBookmark.isEnabled = it }
        viewState.isRootFolder?.let {
            binding.bookmarkBackButton.startAnimation(
                AnimationUtils.createRotationTransitionAnimation(
                    binding.bookmarkBackButton, if (it) {
                        R.drawable.ic_action_star
                    } else {
                        R.drawable.ic_action_back
                    }
                )
            )
        }
        viewState.findInPage?.let {
            if (it.isEmpty()) {
                binding.findBar.isVisible = false
            } else {
                binding.findBar.isVisible = true
                binding.findQuery.text = it
            }
        }
    }

    /**
     * @see BrowserContract.View.renderTabs
     */
    fun renderTabs(tabListState: List<TabViewState>) {
        binding.tabCountView.updateCount(tabListState.size)
        tabsAdapter.submitList(tabListState)
    }

    /**
     * @see BrowserContract.View.showAddBookmarkDialog
     */
    fun showAddBookmarkDialog(title: String, url: String, folders: List<String>) {
        lightningDialogBuilder.showAddBookmarkDialog(
            activity = requireActivity(), currentTitle = title, currentUrl = url, folders = folders, onSave = presenter::onBookmarkConfirmed
        )
    }

    /**
     * @see BrowserContract.View.showBookmarkOptionsDialog
     */
    fun showBookmarkOptionsDialog(bookmark: Bookmark.Entry) {
        lightningDialogBuilder.showLongPressedDialogForBookmarkUrl(activity = requireActivity(), onClick = {
            presenter.onBookmarkOptionClick(bookmark, it)
        })
    }

    /**
     * @see BrowserContract.View.showEditBookmarkDialog
     */
    fun showEditBookmarkDialog(title: String, url: String, folder: String, folders: List<String>) {
        lightningDialogBuilder.showEditBookmarkDialog(
            activity = requireActivity(), currentTitle = title, currentUrl = url, currentFolder = folder, folders = folders, onSave = presenter::onBookmarkEditConfirmed
        )
    }

    /**
     * @see BrowserContract.View.showFolderOptionsDialog
     */
    fun showFolderOptionsDialog(folder: Bookmark.Folder) {
        lightningDialogBuilder.showBookmarkFolderLongPressedDialog(activity = requireActivity(), onClick = {
            presenter.onFolderOptionClick(folder, it)
        })
    }

    /**
     * @see BrowserContract.View.showEditFolderDialog
     */
    fun showEditFolderDialog(oldTitle: String) {
        lightningDialogBuilder.showRenameFolderDialog(
            activity = requireActivity(), oldTitle = oldTitle, onSave = presenter::onBookmarkFolderRenameConfirmed
        )
    }

    /**
     * @see BrowserContract.View.showDownloadOptionsDialog
     */
    fun showDownloadOptionsDialog(download: DownloadEntry) {
        lightningDialogBuilder.showLongPressedDialogForDownloadUrl(activity = requireActivity(), onClick = {
            presenter.onDownloadOptionClick(download, it)
        })
    }

    /**
     * @see BrowserContract.View.showHistoryOptionsDialog
     */
    fun showHistoryOptionsDialog(historyEntry: HistoryEntry) {
        lightningDialogBuilder.showLongPressedHistoryLinkDialog(activity = requireActivity(), onClick = {
            presenter.onHistoryOptionClick(historyEntry, it)
        })
    }

    /**
     * @see BrowserContract.View.showFindInPageDialog
     */
    fun showFindInPageDialog() {
        BrowserDialog.showEditText(
            requireActivity(), R.string.action_find, R.string.search_hint, R.string.search_hint, presenter::onFindInPage
        )
    }

    /**
     * @see BrowserContract.View.showLinkLongPressDialog
     */
    fun showLinkLongPressDialog(longPress: LongPress) {
        BrowserDialog.show(requireActivity(), longPress.targetUrl?.replace(HTTP, ""), DialogItem(title = R.string.dialog_open_new_tab) {
            presenter.onLinkLongPressEvent(
                longPress, BrowserContract.LinkLongPressEvent.NEW_TAB
            )
        }, DialogItem(title = R.string.dialog_open_background_tab) {
            presenter.onLinkLongPressEvent(
                longPress, BrowserContract.LinkLongPressEvent.BACKGROUND_TAB
            )
        }, DialogItem(
            title = R.string.dialog_open_incognito_tab, isConditionMet = !isIncognito()
        ) {
            presenter.onLinkLongPressEvent(
                longPress, BrowserContract.LinkLongPressEvent.INCOGNITO_TAB
            )
        }, DialogItem(title = R.string.action_share) {
            presenter.onLinkLongPressEvent(longPress, BrowserContract.LinkLongPressEvent.SHARE)
        }, DialogItem(title = R.string.dialog_copy_link) {
            presenter.onLinkLongPressEvent(
                longPress, BrowserContract.LinkLongPressEvent.COPY_LINK
            )
        })
    }

    /**
     * @see BrowserContract.View.showImageLongPressDialog
     */
    fun showImageLongPressDialog(longPress: LongPress) {
        BrowserDialog.show(requireActivity(), longPress.targetUrl?.replace(HTTP, ""), DialogItem(title = R.string.dialog_open_new_tab) {
            presenter.onImageLongPressEvent(
                longPress, BrowserContract.ImageLongPressEvent.NEW_TAB
            )
        }, DialogItem(title = R.string.dialog_open_background_tab) {
            presenter.onImageLongPressEvent(
                longPress, BrowserContract.ImageLongPressEvent.BACKGROUND_TAB
            )
        }, DialogItem(
            title = R.string.dialog_open_incognito_tab, isConditionMet = !isIncognito()
        ) {
            presenter.onImageLongPressEvent(
                longPress, BrowserContract.ImageLongPressEvent.INCOGNITO_TAB
            )
        }, DialogItem(title = R.string.action_share) {
            presenter.onImageLongPressEvent(
                longPress, BrowserContract.ImageLongPressEvent.SHARE
            )
        }, DialogItem(title = R.string.dialog_copy_link) {
            presenter.onImageLongPressEvent(
                longPress, BrowserContract.ImageLongPressEvent.COPY_LINK
            )
        }, DialogItem(title = R.string.dialog_download_image) {
            presenter.onImageLongPressEvent(
                longPress, BrowserContract.ImageLongPressEvent.DOWNLOAD
            )
        })
    }

    /**
     * @see BrowserContract.View.showCloseBrowserDialog
     */
    fun showCloseBrowserDialog(id: Int) {
        BrowserDialog.show(requireActivity(), R.string.dialog_title_close_browser, DialogItem(title = R.string.close_tab) {
            presenter.onCloseBrowserEvent(id, BrowserContract.CloseTabEvent.CLOSE_CURRENT)
        }, DialogItem(title = R.string.close_other_tabs) {
            presenter.onCloseBrowserEvent(id, BrowserContract.CloseTabEvent.CLOSE_OTHERS)
        }, DialogItem(title = R.string.close_all_tabs, onClick = {
            presenter.onCloseBrowserEvent(id, BrowserContract.CloseTabEvent.CLOSE_ALL)
        })
        )
    }

    /**
     * @see BrowserContract.View.openBookmarkDrawer
     */
    fun openBookmarkDrawer() {
        binding.drawerLayout.closeDrawer(binding.tabDrawer)
        binding.drawerLayout.openDrawer(binding.bookmarkDrawer)
    }

    /**
     * @see BrowserContract.View.closeBookmarkDrawer
     */
    fun closeBookmarkDrawer() {
        binding.drawerLayout.closeDrawer(binding.bookmarkDrawer)
    }

    /**
     * @see BrowserContract.View.openTabDrawer
     */
    fun openTabDrawer() {
        binding.drawerLayout.closeDrawer(binding.bookmarkDrawer)
        binding.drawerLayout.openDrawer(binding.tabDrawer)
    }

    /**
     * @see BrowserContract.View.closeTabDrawer
     */
    fun closeTabDrawer() {
        binding.drawerLayout.closeDrawer(binding.tabDrawer)
    }

    /**
     * @see BrowserContract.View.showToolbar
     */
    fun showToolbar() {
        tabPager.showToolbar()
    }

    /**
     * @see BrowserContract.View.showToolsDialog
     */
    fun showToolsDialog(areAdsAllowed: Boolean, shouldShowAdBlockOption: Boolean) {
        val whitelistString = if (areAdsAllowed) {
            R.string.dialog_adblock_enable_for_site
        } else {
            R.string.dialog_adblock_disable_for_site
        }

        BrowserDialog.showWithIcons(
            requireContext(), getString(R.string.dialog_tools_title), DialogItem(
                icon = requireContext().drawable(R.drawable.ic_action_desktop),
                title = R.string.dialog_toggle_desktop,
                onClick = presenter::onToggleDesktopAgent
            ), DialogItem(
                icon = requireContext().drawable(R.drawable.ic_block),
                colorTint = requireContext().color(R.color.error_red).takeIf { areAdsAllowed },
                title = whitelistString,
                isConditionMet = shouldShowAdBlockOption,
                onClick = presenter::onToggleAdBlocking
            )
        )
    }

    /**
     * @see BrowserContract.View.showLocalFileBlockedDialog
     */
    fun showLocalFileBlockedDialog() {
        AlertDialog.Builder(requireContext()).setCancelable(true).setTitle(R.string.title_warning).setMessage(R.string.message_blocked_local)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                presenter.onConfirmOpenLocalFile(allow = false)
            }.setPositiveButton(R.string.action_open) { _, _ ->
                presenter.onConfirmOpenLocalFile(allow = true)
            }.setOnCancelListener { presenter.onConfirmOpenLocalFile(allow = false) }.resizeAndShow()
    }

    /**
     * @see BrowserContract.View.showFileChooser
     */
    fun showFileChooser(intent: Intent) {
        launcher.launch(intent)
    }

    /**
     * @see BrowserContract.View.showCustomView
     */
    fun showCustomView(view: View) {
        binding.root.addView(view)
        customView = view
    }

    /**
     * @see BrowserContract.View.hideCustomView
     */
    fun hideCustomView() {
        customView?.let(binding.root::removeView)
        customView = null
    }

    /**
     * @see BrowserContract.View.clearSearchFocus
     */
    fun clearSearchFocus() {
        binding.search.clearFocus()
    }

    private fun animateColorChange(color: Int) {
        if (!userPreferences.colorModeEnabled || userPreferences.useTheme != AppTheme.LIGHT || isIncognito()) {
            return
        }
        val shouldShowTabsInDrawer = userPreferences.showTabsInDrawer
        val adapter = tabsAdapter as? DesktopTabRecyclerViewAdapter
        val colorAnimator = ColorAnimator(defaultColor)
        binding.toolbar.startAnimation(colorAnimator.animateTo(
            color
        ) { mainColor, secondaryColor ->
            if (shouldShowTabsInDrawer) {
                backgroundDrawable.color = mainColor
                //  window.setBackgroundDrawable(backgroundDrawable)
            } else {
                adapter?.updateForegroundTabColor(mainColor)
            }
            binding.toolbar.setBackgroundColor(mainColor)
            binding.searchContainer.background?.tint(secondaryColor)
        })
    }

    private fun ImageView.updateVisibilityForDrawable() {
        visibility = if (drawable == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}