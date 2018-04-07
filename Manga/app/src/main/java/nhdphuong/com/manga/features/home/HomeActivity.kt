package nhdphuong.com.manga.features.home

import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.util.Log
import nhdphuong.com.manga.NHentaiApp
import nhdphuong.com.manga.R
import javax.inject.Inject
import android.content.Intent
import android.view.KeyEvent


class HomeActivity : AppCompatActivity() {
    companion object {
        private val TAG = HomeActivity::class.java.simpleName
    }

    @Suppress("unused")
    @Inject
    lateinit var mPresenter: HomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate")
        setContentView(R.layout.activity_home)
        showFragment()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.e(TAG, "onSaveInstanceState 3 params")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.e(TAG, "onSaveInstanceState 1 param")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e(TAG, "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e(TAG, "onAttachedToWindow")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.e(TAG, "onDetachedFromWindow")
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        Log.e(TAG, "onAttachFragment")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.e(TAG, "onConfigurationChanged")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.e(TAG, "onWindowFocusChanged hasFocus=$hasFocus")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.e(TAG, "onKeyDown keyCode=$keyCode, event=${event.action}")
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                return if (isTaskRoot) {
                    val homeIntent = Intent(Intent.ACTION_MAIN)
                            .addCategory(Intent.CATEGORY_HOME)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(homeIntent)
                    true
                } else {
                    super.onKeyDown(keyCode, event)
                    false
                }
            }

            else -> {
                super.onKeyDown(keyCode, event)
                return false
            }
        }

    }

    private fun showFragment() {
        var homeFragment = supportFragmentManager.findFragmentById(R.id.clMainFragment) as HomeFragment?
        if (homeFragment == null) {
            homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.clMainFragment, homeFragment, TAG)
                    .addToBackStack(TAG)
                    .commitAllowingStateLoss()
        }

        NHentaiApp.instance.applicationComponent.plus(HomeModule(homeFragment)).inject(this)
    }
}
