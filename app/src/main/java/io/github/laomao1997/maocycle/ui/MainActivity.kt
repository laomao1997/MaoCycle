package io.github.laomao1997.maocycle.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import io.github.laomao1997.maocycle.R

import io.github.laomao1997.maocycle.utils.ToastUtil
import io.github.laomao1997.maocycle.ui.fragments.AllDataFragment
import io.github.laomao1997.maocycle.ui.fragments.BigSpeedFragment
import io.github.laomao1997.maocycle.transformer.ZoomOutPageTransformer
import io.github.laomao1997.maocycle.ui.fragments.MapFragment
import java.lang.StringBuilder

/**
 * 页面上 ViewPager2 所拥有的页面数量
 */
private const val NUM_PAGES = 3

/**
 * 主页面
 *
 * @author ZHANG Jinghao - zhang.jing.hao@outlook.com
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mViewModel: MainViewModel

    private lateinit var mViewPager: ViewPager2
    private lateinit var mTextLocation: TextView
    private lateinit var mButtonArrow: Button
    private lateinit var mButtonStart: Button

    private var mMapFragment: Fragment? = null
    private var mBigSpeedFragment: Fragment? = null
    private var mAllDataFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(application)
        ).get(MainViewModel::class.java)

        initView()
        requestPermission()
        mViewModel.currentLocation.observe(this) {
            val altitude = it.altitude
            val longitude = it.longitude
            val latitude = it.latitude

            val locationString = "Altitude: $altitude, Longitude: $longitude, Latitude: $latitude."
            mTextLocation.text = locationString
        }
    }

    override fun onBackPressed() {
        when (mViewPager.currentItem) {
            0 -> {
                mViewPager.currentItem = 1
            }
            1 -> {
                // If the user is currently looking at the second step, allow the system to handle
                // the Back button. This calls finish() on this activity and pops the back stack.
                super.onBackPressed()
            }
            else -> {
                // Otherwise, select the previous step.
                mViewPager.currentItem = mViewPager.currentItem - 1
            }
        }
    }

    private fun initView() {
        mViewPager = findViewById(R.id.view_pager)
        mButtonArrow = findViewById(R.id.btn_arrow)
        mButtonStart = findViewById(R.id.btn_start)
        mTextLocation = findViewById(R.id.text_location)

        val onClickListenerImpl = OnClickListenerImpl()
        mButtonArrow.setOnClickListener(onClickListenerImpl)
        mButtonStart.setOnClickListener(onClickListenerImpl)

        val viewPagerAdapter = ViewPagerAdapter(this)
        val changePageCallback = ViewPagerChangePageCallback()
        mViewPager.adapter = viewPagerAdapter
        mViewPager.registerOnPageChangeCallback(changePageCallback)
        mViewPager.currentItem = 1
        mViewPager.setPageTransformer(ZoomOutPageTransformer())
    }

    private fun requestPermission() {
        AndPermission.with(this)
            .runtime()
            .permission(
                Permission.ACCESS_COARSE_LOCATION,
                Permission.ACCESS_FINE_LOCATION,
                Permission.READ_PHONE_STATE
            )
            .onDenied { denied ->
                val sb = StringBuilder()
                for (deny: String in denied) {
                    sb.append(deny).append(',')
                }
                ToastUtil.showToast("onDenied: $sb")
                mViewModel.startLocating()
            }
            .onGranted { granted ->
                val sb = StringBuilder()
                for (grant: String in granted) {
                    sb.append(grant).append(',')
                }
                ToastUtil.showToast("onGranted: $sb")
                mViewModel.startLocating()
            }
            .start()
    }

    /**
     * 设置允许或禁用ViewPager的滑动效果
     */
    private fun allowViewPagerSlide(isAllowed: Boolean = true) {
        mViewPager.isUserInputEnabled = isAllowed;
    }

    private inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> {
                if (mMapFragment == null) {
                    mMapFragment = MapFragment()
                }
                mMapFragment!!
            }
            1 -> {
                if (mAllDataFragment == null) {
                    mAllDataFragment = AllDataFragment()
                }
                mAllDataFragment!!
            }
            else -> {
                if (mBigSpeedFragment == null) {
                    mBigSpeedFragment = BigSpeedFragment()
                }
                mBigSpeedFragment!!
            }
        }
    }

    private inner class ViewPagerChangePageCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            mViewPager.isUserInputEnabled = (position != 0)
            mButtonArrow.visibility = if (position == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
            allowViewPagerSlide(position != 0)
        }
    }

    private inner class OnClickListenerImpl : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v) {
                mButtonArrow -> {
                    mViewPager.currentItem = 1
                }
                mButtonStart -> {

                }
            }
        }

    }
}