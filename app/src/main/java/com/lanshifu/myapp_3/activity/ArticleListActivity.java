package com.lanshifu.myapp_3.activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.baselibrary.network.RxScheduler;
import com.lanshifu.myapp_3.R;
import com.lanshifu.myapp_3.model.Article;
import com.lanshifu.myapp_3.network.MyObserver;
import com.lanshifu.myapp_3.network.RetrofitHelper;
import com.lanshifu.myapp_3.widget.CommRecyclerView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by lanxiaobin on 2018/1/26.
 */

public class ArticleListActivity extends BaseActivity {
    @Bind(R.id.recyclerview)
    CommRecyclerView mRecyclerview;
    private BaseQuickAdapter<Article.ArticlesBean, BaseViewHolder> mAdapter;

    private int mCurrentPage = 1;
    private int mPageCount = 20;

    @Override
    protected int getLayoutId() {
        return R.layout.actvity_articlelist;
    }

    @Override
    protected void initView() {

        setTitleText("文章列表");

        mAdapter = new BaseQuickAdapter<Article.ArticlesBean, BaseViewHolder>(R.layout.item_article, new ArrayList<Article.ArticlesBean>()) {
                    @Override
                    protected void convert(BaseViewHolder helper, Article.ArticlesBean item) {
                        helper.setText(R.id.tv_title,item.getArticle_title());
                        helper.setText(R.id.tv_content,item.getArticle_content());
                        helper.setText(R.id.tv_auth,item.getArticle_author());
                    }
                };

        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.autoRefresh();
        mRecyclerview.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadData(true);
            }
        });
        mRecyclerview.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadData(false);
            }
        });

    }


    private void loadData(final boolean refersh){
        if (refersh){
            mCurrentPage = 1;
        }
        RetrofitHelper.getInstance().getDefaultApi().getArticles(mCurrentPage, mPageCount)
                .compose(RxScheduler.<Article>io_main())
                .subscribe(new MyObserver<Article>() {
                    @Override
                    public void _onNext(Article articles) {
                        mRecyclerview.finishRefresh();
                        mRecyclerview.finishLoadmore();
                        if (articles.getArticles().size()> 0){
                            mCurrentPage ++;
                        }else {
                            showShortToast("没有数据了");
                        }
                        if (refersh){
                            mAdapter.replaceData(articles.getArticles());
                        }else {
                            mAdapter.addData(articles.getArticles());
                        }

                    }

                    @Override
                    public void _onError(String e) {
                        mRecyclerview.finishRefresh();
                        mRecyclerview.finishLoadmore();
                    }
                });


    }
}
