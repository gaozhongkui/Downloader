package com.almighty.downloader.browser.di

import com.almighty.downloader.BrowserApp
import com.almighty.downloader.ThemableBrowserActivity
import com.almighty.downloader.adblock.BloomFilterAdBlocker
import com.almighty.downloader.adblock.NoOpAdBlocker
import com.almighty.downloader.browser.search.SearchBoxModel
import com.almighty.downloader.device.BuildInfo
import com.almighty.downloader.dialog.LightningDialogBuilder
import com.almighty.downloader.search.SuggestionsAdapter
import com.almighty.downloader.settings.activity.ThemableSettingsActivity
import com.almighty.downloader.settings.fragment.AdBlockSettingsFragment
import com.almighty.downloader.settings.fragment.AdvancedSettingsFragment
import com.almighty.downloader.settings.fragment.BookmarkSettingsFragment
import com.almighty.downloader.settings.fragment.DebugSettingsFragment
import com.almighty.downloader.settings.fragment.DisplaySettingsFragment
import com.almighty.downloader.settings.fragment.GeneralSettingsFragment
import com.almighty.downloader.settings.fragment.PrivacySettingsFragment
import com.almighty.downloader.settings.fragment.RootSettingsFragment
import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, AppBindsModule::class, Submodules::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun buildInfo(buildInfo: BuildInfo): Builder

        fun build(): AppComponent
    }

    fun inject(fragment: BookmarkSettingsFragment)

    fun inject(builder: LightningDialogBuilder)

    fun inject(activity: ThemableBrowserActivity)

    fun inject(advancedSettingsFragment: AdvancedSettingsFragment)

    fun inject(app: BrowserApp)

    fun inject(activity: ThemableSettingsActivity)

    fun inject(fragment: PrivacySettingsFragment)

    fun inject(fragment: DebugSettingsFragment)

    fun inject(suggestionsAdapter: SuggestionsAdapter)

    fun inject(searchBoxModel: SearchBoxModel)

    fun inject(activity: RootSettingsFragment)

    fun inject(generalSettingsFragment: GeneralSettingsFragment)

    fun inject(displaySettingsFragment: DisplaySettingsFragment)

    fun inject(adBlockSettingsFragment: AdBlockSettingsFragment)

    fun provideBloomFilterAdBlocker(): BloomFilterAdBlocker

    fun provideNoOpAdBlocker(): NoOpAdBlocker

    fun browser2ComponentBuilder(): Browser2Component.Builder

}

@Module(subcomponents = [Browser2Component::class])
internal class Submodules
