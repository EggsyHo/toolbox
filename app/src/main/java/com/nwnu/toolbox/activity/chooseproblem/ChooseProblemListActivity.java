package com.nwnu.toolbox.activity.chooseproblem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nwnu.toolbox.R;
import com.nwnu.toolbox.activity.base.BaseActivity;
import com.nwnu.toolbox.adapter.chooseproblem.ItemListAdapter;
import com.nwnu.toolbox.db.chooseproblem.ChooseProblemItem;
import com.nwnu.toolbox.dialog.chooseproblem.AddItemDialog;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ChooseProblemListActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private Button backBtn;
    private TextView title, nothingHint, addBtn;

    private AddItemDialog addItemDialog;

    @Override
    protected void findViewById() {
        backBtn = $(R.id.title_layout_back_button);
        addBtn = $(R.id.title_layout_option_button);
        title = $(R.id.title_layout_title_text);
        nothingHint = $(R.id.choose_problem_no_item_hint);
        recyclerView = $(R.id.choose_problem_recycler_view);
    }

    @Override
    protected void initView() {
        backBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        addBtn.setVisibility(View.VISIBLE);
        addBtn.setText("添加");
        title.setText("管理");
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMenuData();
    }

    private void refreshMenuData() {
        final List<ChooseProblemItem> items = DataSupport.where("parentId = 0").find(ChooseProblemItem.class);
        if (items == null || items.size() == 0) {
            nothingHint.setVisibility(View.VISIBLE);
        } else {
            nothingHint.setVisibility(View.GONE);
            final ItemListAdapter adapter = new ItemListAdapter(items, true);
            adapter.setOnItemClickListener(new ItemListAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int data) {
                    Intent intent = new Intent(mContext, ChooseProblemDetailListActivity.class);
                    intent.putExtra("parentId", items.get(data).getId());
                    intent.putExtra("title", items.get(data).getTitle());
                    startActivity(intent);
                }
            });
            adapter.setOnItemLongClickListener(new ItemListAdapter.OnRecyclerItemLongListener() {
                @Override
                public void onItemLongClick(View view, final int position) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("删除提示")
                            .setMessage("你确定需要删除:  " + items.get(position).getTitle())
                            .setPositiveButton("是的，删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DataSupport.deleteAll(ChooseProblemItem.class, "parentId = ?", items.get(position).getId() + "");
                                    DataSupport.delete(ChooseProblemItem.class, items.get(position).getId());
                                    items.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_problem_list_activity);
        findViewById();
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_layout_option_button:
                addItem();
                break;
            case R.id.title_layout_back_button:
                finish();
                break;
        }
    }

    private void addItem() {
        addItemDialog = new AddItemDialog(mContext);
        addItemDialog.setOnCancelClickedListener(new AddItemDialog.onCancelClickedListener() {
            @Override
            public void onClick() {
                addItemDialog.dismiss();
            }
        });
        addItemDialog.setOnAddClickedListener(new AddItemDialog.onAddClickedListener() {
            @Override
            public void onClick(String title) {
                ChooseProblemItem item = new ChooseProblemItem();
                item.setParentId(0);
                item.setTitle(title);
                if (item.save()) {
                    refreshMenuData();
                    addItemDialog.dismiss();
                }
            }
        });
        addItemDialog.show();
    }

}
