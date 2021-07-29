package org.xfort.rock.imagepicker.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.xfort.rock.imagepicker.MediaItem
import org.xfort.rock.imagepicker.R
import org.xfort.xrock.listener.OnDataCallback

/**
 ** Created by ZhangHuaXin on 2021/5/17.
 **/
class MediaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medias)

        val checkedUris = intent.getParcelableArrayListExtra<Uri>("checkedUris")
        val count = intent.getIntExtra("max", 1)

        val frag = MediaListFragment.newInstance(checkedUris, count)
        frag.onDataCallback = object : OnDataCallback<List<MediaItem>> {
            override fun onDataCallback(data: List<MediaItem>?) {
                val intent = data?.let {
                    Intent().putParcelableArrayListExtra("data", ArrayList(it))
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.frame_content, frag).commit()
    }
}