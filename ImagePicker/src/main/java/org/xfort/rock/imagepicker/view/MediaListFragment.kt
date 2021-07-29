package org.xfort.rock.imagepicker.view

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.app.LoaderManager.LoaderCallbacks
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import org.xfort.rock.imagepicker.*
import org.xfort.rock.imagepicker.adapter.MediasAdapter
import org.xfort.rock.imagepicker.adapter.ThumnailAdapter
import org.xfort.xrock.listener.OnDataCallback
import org.xfort.xrock.listener.OnItemViewClickListener

/**
 ** Created by ZhangHuaXin on 2021/5/7.
 * 相册列表
 **/
class MediaListFragment : Fragment(), View.OnClickListener, OnMediaSelectListener {
    private lateinit var adapter: MediasAdapter
    lateinit var onDataCallback: OnDataCallback<List<MediaItem>>
    private lateinit var mediaLoader: LoaderManager

    private val mediasRecyclerView by lazy { view?.findViewById<RecyclerView>(R.id.all_medias) }
    private val selectedMediasRecyclerView by lazy { view?.findViewById<RecyclerView>(R.id.selected_medias) }
    private val closeIV by lazy { view?.findViewById<View>(R.id.close) }
    private val okTV by lazy { view?.findViewById<TextView>(R.id.ok) }
    private val cameraTV by lazy { view?.findViewById<View>(R.id.camera) }

    private val thumnailAdapter by lazy { ThumnailAdapter() }
    private val captureHandler = PhotoTaker()

    private var maxCount = 2

    companion object {
        /**
         * 默认选中的；最大选择数
         */
        fun newInstance(checkedImgs: List<Uri>?, maxCount: Int): MediaListFragment {
            var fragment = MediaListFragment()
            fragment.arguments = bundleOf("checkedUris" to checkedImgs, "max" to maxCount)
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_media_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeIV?.setOnClickListener(this)
        okTV?.setOnClickListener(this)
        if (MediaStoreUtils.hasCamera(requireContext())) {
            cameraTV?.visibility = View.VISIBLE
            cameraTV?.setOnClickListener(this)
        } else {
            cameraTV?.visibility = View.GONE
        }

        val minDP = resources.getDimension(R.dimen.meta)
        val displayWidth = resources.displayMetrics.widthPixels
        val itemWidth = minDP * 32 * 4
        var spanCount = displayWidth / itemWidth
        if (spanCount < 2) {
            spanCount = 2F
        } //        itemWidth = displayWidth / spanCount
        val mediasLM =
            StaggeredGridLayoutManager(spanCount.toInt(), StaggeredGridLayoutManager.VERTICAL)
        mediasRecyclerView?.layoutManager = mediasLM
        mediasRecyclerView?.setHasFixedSize(true)

        val spaceItem = DividerItemDecoration()
        spaceItem.layoutType = DividerItemDecoration.GRID
        spaceItem.layoutOrientation = DividerItemDecoration.VERTICAL
        spaceItem.gridSpanCount = mediasLM.spanCount
        spaceItem.itemSpaceSize = Pair((minDP * 2).toInt(), (minDP * 2).toInt())
        spaceItem.headerSpaceSize = Pair((minDP * 40).toInt(), (minDP * 48).toInt())
        spaceItem.footerSpaceSize = Pair((minDP * 100).toInt(), (minDP * 100).toInt())
        mediasRecyclerView?.addItemDecoration(spaceItem)

        val glide = Glide.with(this)
        adapter = MediasAdapter(viewLifecycleOwner.lifecycle)
        adapter.glide = glide
        adapter.mediaSelectListener = this
        adapter.onItemViewClick = object : OnItemViewClickListener<Uri> {
            override fun onItemViewClick(v: View?, position: Int, data: Uri?) {
                when (v?.id) {
                    R.id.image -> goPreview(data)
                }
            }
        }

        mediasRecyclerView?.adapter = adapter
        mediasRecyclerView?.addOnScrollListener(GlideScrollListener(glide))

        val thumbnailsLM = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dividerItem = DividerItemDecoration()
        dividerItem.layoutType = DividerItemDecoration.LINEAR
        dividerItem.layoutOrientation = DividerItemDecoration.HORIZONTAL
        dividerItem.itemSpaceSize = Pair((minDP * 16).toInt(), (minDP * 16).toInt())
        dividerItem.headerSpaceSize = Pair((minDP * 16).toInt(), (minDP * 16).toInt())
        dividerItem.footerSpaceSize = Pair((minDP * 16).toInt(), (minDP * 16).toInt())

        selectedMediasRecyclerView?.addItemDecoration(dividerItem)
        selectedMediasRecyclerView?.setHasFixedSize(true)
        selectedMediasRecyclerView?.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)
        selectedMediasRecyclerView?.layoutManager = thumbnailsLM
        selectedMediasRecyclerView?.adapter = thumnailAdapter

        thumnailAdapter.itemViewClick = object : OnItemViewClickListener<String> {
            override fun onItemViewClick(v: View?, position: Int, data: String?) {
                onThumnailViewClick(v, position)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var defaultImgs: List<Uri>? = null
        arguments?.let {
            maxCount = it.getInt("max", 2)
            defaultImgs = it.getParcelableArrayList<Uri>("checkedUris")
        }
        defaultImgs?.forEach {
            adapter.setDefaultCheck(it)
        }
        adapter.checkMaxCount = maxCount
        mediaLoader = LoaderManager.getInstance(this)

        requestAlbumPermission()
    }


    /**
     * 读取相册
     */
    fun requestAlbumPermission() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (AndPermission.hasPermissions(this, perms)) {
            loadImages()
        } else {
            AndPermission.with(this).runtime().permission(perms).onGranted {
                loadImages()
            }.onDenied {
                if (AndPermission.hasAlwaysDeniedPermission(context, perms.toList())) {
                    Toast.makeText(
                        context, resources.getString(
                            R.string.request_permissions_x,
                            Permission.transformText(context, it).toString()
                        ), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    requestAlbumPermission()
                }
            }.rationale { context, data, executor ->
                executor.execute()
            }.start()
        }
    }

    fun loadImages() {
        mediaLoader.initLoader(1, null, object : LoaderCallbacks<Cursor> {
            override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
                return MediaLoader.newInstance(requireContext(), viewLifecycleOwner.lifecycle)
            }

            override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                adapter.swapCursor(data)
            }

            override fun onLoaderReset(loader: Loader<Cursor>) {
                adapter.swapCursor(null)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            closeIV -> close()
            okTV -> ok()
            cameraTV -> capture()
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PhotoTaker.REQUEST_CAPTURE) {
            captureHandler.onActivityResult(resultCode, data)
            val photoUri = captureHandler.currentPhotoUri
            photoUri?.let {
                adapter.setDefaultCheck(photoUri)
                loadImages()
            }
        }
    }

    fun close() {
        requireActivity().onBackPressed()
    }

    /**
     * 拍照
     */
    fun capture() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (AndPermission.hasPermissions(this, perms)) {
            captureHandler.startCapture(this)
        } else {
            AndPermission.with(this).runtime().permission(perms).onGranted {
                capture()
            }.onDenied {
                if (AndPermission.hasAlwaysDeniedPermission(context, perms.toList())) {
                    Toast.makeText(
                        context, resources.getString(
                            R.string.request_permissions_x,
                            Permission.transformText(context, it).toString()
                        ), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    capture()
                }
            }.rationale { context, data, executor ->
                executor.execute()
            }.start()
        }
    }


    fun ok() {
        onDataCallback?.onDataCallback(adapter.checkedMedias.toList())
    }

    /**
     * 选中图片
     */
    override fun onSelecteChange(item: MediaItem?) {
        okTV?.text = resources.getString(R.string.ok) + "\t" + adapter.checkedMedias.size
        if (thumnailAdapter.dataList.isEmpty()) {
            thumnailAdapter.dataList.addAll(adapter.checkedMedias)
            thumnailAdapter.notifyDataSetChanged()
        } else {
            val obj = thumnailAdapter.dataList.find { it.hashCode() == item.hashCode() }
            if (obj == null) {
                item?.let {
                    thumnailAdapter.dataList.add(it)
                    thumnailAdapter.notifyItemInserted(thumnailAdapter.itemCount - 1)
                    selectedMediasRecyclerView?.post {
                        selectedMediasRecyclerView?.invalidateItemDecorations()
                        selectedMediasRecyclerView?.scrollToPosition(thumnailAdapter.itemCount - 1)
                    }
                }
            } else {
                val index = thumnailAdapter.dataList.indexOf(obj)
                selectedMediasRecyclerView?.scrollToPosition(index)
                selectedMediasRecyclerView?.post {
                    thumnailAdapter.dataList.remove(obj)
                    thumnailAdapter.notifyItemRemoved(index)
                    if (index >= thumnailAdapter.itemCount) {
                        selectedMediasRecyclerView?.post {
                            selectedMediasRecyclerView?.invalidateItemDecorations()
                        }
                    }
                }
            }
        }
    }

    fun onThumnailViewClick(v: View?, position: Int) {
        var itemMedia = thumnailAdapter.dataList[position]
        if (itemMedia.appAdapterPosition >= 0 && itemMedia.appAdapterPosition < adapter.itemCount) {
            mediasRecyclerView?.scrollToPosition(itemMedia.appAdapterPosition)
        }
    }

    fun goPreview(imgUri: Uri?) {
        imgUri?.let {
            PicPreviewFragment.newInstance(imgUri).show(childFragmentManager, "bigpic")
        }
    }
}