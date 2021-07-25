package io.github.laomao1997.maocycle.utils

import android.widget.Toast
import io.github.laomao1997.maocycle.MaoCycleApplication

object ToastUtil {
    fun showToast(message: String) {
        Toast.makeText(MaoCycleApplication.context, message, Toast.LENGTH_LONG).show();
    }
}