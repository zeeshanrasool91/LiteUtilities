package com.thetechnocafe.gurleensethi.liteutilities

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.TextView
import com.thetechnocafe.gurleensethi.liteutils.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val list: ArrayList<String> = ArrayList()
        list.add("Test")
        list.add("1")
        list.add("2")
        list.add("3")
        list.add("This is a test")
        list.add("123")
        //Builder Approach
        /*   recyclerView.layoutManager = LinearLayoutManager(this)
           val recyclerAdapter = RecyclerAdapterUtil<String>(this, list, R.layout.item_recycler_view)
           recyclerAdapter.addOnDataBindListener { itemView, item, position, viewsMap ->
               val textView = viewsMap[R.id.textView] as TextView
               textView.text = item
           }
           RecyclerAdapterUtil.Builder(this, list, R.layout.item_recycler_view)
                   .viewsList(R.id.textView)
                   .bindView { itemView, item, position, viewsMap ->
                       val textView = viewsMap[R.id.textView] as TextView
                       textView.text = item
                   }
                   .addClickListener { item, position ->
                       coloredShortToast(item, android.R.color.darker_gray, android.R.color.black)
                       //Take action when item is pressed
                   }
                   .addLongClickListener { item, position ->
                       //Take action when item is long pressed
                   }
                   .into(recyclerView)*/
        //new Approach
        val recyclerAdapter2 = RecyclerAdapterUtil(this, list, R.layout.item_recycler_view, R.layout.loading_layout)
        recyclerAdapter2.addViewsList(R.id.textView)
        //recyclerAdapter2.addSecondViewsList(R.id.progressBar)
        recyclerAdapter2.addOnDataBindListener { itemView, item, position, viewsMap ->
            if(recyclerAdapter2.getItemViewType(position) == RecyclerAdapterUtil.VIEW_TYPE_ITEM) {
                val textView = viewsMap[R.id.textView] as TextView
                textView.text = item
            }
            //recyclerAdapter2.getItemViewType(position)
        }
        recyclerView.adapter = recyclerAdapter2
        initScrollListener(recyclerAdapter2)

        shortToast("This is a short toast")
        longToast("This is a long toast")

        sharedPreferences("SP", Context.MODE_PRIVATE) {
            putString("string", "Some Value 123")
            putInt("integer", 1)
        }

        defaultSharedPreferences {
            putString("string", "in default sp")
        }

        getFromSharedPreferences<String>("SP", "string", "default")
        getFromDefaultSharedPreferences<String>("string", "default")

        button.setOnClickListener {
            var result = editText.validator()
                    .atLeastOneUpperCase()
                    .atLeastOneLowerCase()
                    .maximumLength(20)
                    .minimumLength(5)
                    .noNumbers()
                    .addSuccessCallback {
                        //Proceed
                    }
                    .addErrorCallback { errorType ->
                        when (errorType) {
                            ValidationError.AT_LEAST_ONE_LOWER_CASE -> {
                                editText.error = "Please provide at-least one lower case letter"
                            }
                            ValidationError.AT_LEAST_ONE_UPPER_CASE -> {
                                editText.error = "Please provide at-least one upper case letter"
                            }
                            else -> {
                                editText.error = "Not Enough"
                            }
                        }
                    }
                    .validate()
        }

        buttonAdd.setOnClickListener {
            val list2: ArrayList<String> = ArrayList()
            list2.add("Test")
            list2.add("1")
            list2.add("2")
            list2.add("3")
            list2.add("This is a test")
            list2.add("123")
            recyclerAdapter2.addUpdateItemsList(list2)
        }

        buttonClear.setOnClickListener {
            recyclerAdapter2.clearItemsList()
        }

        buttonReset.setOnClickListener {
            recyclerAdapter2.resetItemsList(list)
        }

        LogUtils.addLevel(LogLevel.ALL)
        LogUtils.addLevel(LogLevel.DEBUG)
        LogUtils.addLevel(LogLevel.INFO)
        LogUtils.addLevel(LogLevel.ERROR)
        LogUtils.addLevel(LogLevel.VERBOSE)
        LogUtils.addLevel(LogLevel.WARN)
        LogUtils.addLevel(LogLevel.WTF)
        debug("This is a debug message")
        error("Some error occurred")
        warn("This is a warning")
        info("Some information")
        verbose("VERBOSE!")
        wtf("Ignore this")
        json("{message:'This is a message', version: {num: 10}}")
        shout("Shout this message loud!\nThank YOU")
        //exception(Exception("ERROR"))

        /*val validator = Validator(passwordEditText.text.toString())
        validator.atLeastOneNumber()
                .atLeastOneUpperCase()
                .minimumLength(8)
                .maximumLength(32)
                .atLeastOneSpecialCharacter()*/
    }

    private fun initScrollListener(recyclerAdapterUtil: RecyclerAdapterUtil<String>) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager: LinearLayoutManager? = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == recyclerAdapterUtil.itemList.size - 1) {
                        //bottom of list!
                        loadMore(recyclerAdapterUtil)
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore(recyclerAdapterUtil: RecyclerAdapterUtil<String>) {
        recyclerAdapterUtil.itemList.add("")
        recyclerAdapterUtil.notifyItemInserted(recyclerAdapterUtil.itemList.size - 1)
        val handler = Handler()
        handler.postDelayed({
            recyclerAdapterUtil.itemList.removeAt(recyclerAdapterUtil.itemList.size - 1)
            val scrollPosition: Int = recyclerAdapterUtil.itemList.size
            recyclerAdapterUtil.notifyItemRemoved(scrollPosition)
            var currentSize = scrollPosition
            val nextLimit = currentSize + 10
            while (currentSize - 1 < nextLimit) {
                recyclerAdapterUtil.itemList.add("Item $currentSize")
                currentSize++
            }
            recyclerAdapterUtil.notifyDataSetChanged()
            isLoading = false
        }, 2000)
    }

}
