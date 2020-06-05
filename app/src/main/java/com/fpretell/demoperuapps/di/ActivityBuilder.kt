package com.fpretell.demoperuapps.di
import com.fpretell.demoperuapps.ui.main.MainActivity
import com.fpretell.demoperuapps.ui.login.LoginActivity
import com.fpretell.demoperuapps.ui.new_post.CreatePostActivity
import com.fpretell.demoperuapps.ui.phone_authentication.PhoneAuthenticationActivity
import com.fpretell.demoperuapps.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Module
abstract class  ActivityBuilder {

    @ActivityScope
    @ContributesAndroidInjector/*(modules = [LoginModule::class])*/
    abstract fun bindSplashActivity(): SplashActivity

    @ActivityScope
    @ContributesAndroidInjector/*(modules = [LoginModule::class])*/
    abstract fun bindLoginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindPhoneAuthenticationActivity(): PhoneAuthenticationActivity

    @ActivityScope
    @ContributesAndroidInjector/*(modules = [MainActivityModule::class])*/
    abstract fun bindMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector/*(modules = [MainActivityModule::class])*/
    abstract fun bindCreatePostActivity(): CreatePostActivity
}