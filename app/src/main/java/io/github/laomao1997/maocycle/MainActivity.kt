package io.github.laomao1997.maocycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import io.github.laomao1997.maocycle.fragments.AllDataFragment
import io.github.laomao1997.maocycle.fragments.BigSpeedFragment
import io.github.laomao1997.maocycle.fragments.MapFragment
import io.github.laomao1997.maocycle.utils.ToastUtil
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

    private lateinit var mViewPager: ViewPager2
    private lateinit var mButtonArrow: Button
    private lateinit var mButtonStart: Button

    private var mMapFragment: Fragment? = null
    private var mBigSpeedFragment: Fragment? = null
    private var mAllDataFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        requestPermission()
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
            .permission(Permission.ACCESS_COARSE_LOCATION)
            .onDenied { denied ->
                val sb = StringBuilder()
                for (deny: String in denied) {
                    sb.append(deny).append(',')
                }
                ToastUtil.showToast("onDenied: $sb")
            }
            .onGranted { granted ->
                val sb = StringBuilder()
                for (grant: String in granted) {
                    sb.append(grant).append(',')
                }
                ToastUtil.showToast("onGranted: $sb")
            }
            .start()
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

    private inner class ViewPagerChangePageCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            mButtonArrow.visibility = if (position == 0) {
                 View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private inner class OnClickListenerImpl: View.OnClickListener {
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