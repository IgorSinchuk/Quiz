package com.nonexistentware.igorsinchuk.simplequiz.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nonexistentware.igorsinchuk.simplequiz.Interface.ItemClickListener;
import com.nonexistentware.igorsinchuk.simplequiz.Model.Category;
import com.nonexistentware.igorsinchuk.simplequiz.R;
import com.nonexistentware.igorsinchuk.simplequiz.ViewHolder.CategoryViewHolder;
import com.squareup.picasso.Picasso;

public class CategoryFragment extends Fragment {

    View myFragment;

    RecyclerView listcategory;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference categories;


    public static CategoryFragment newInstance() {
        CategoryFragment categoryFragment = new CategoryFragment();
        return categoryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Category");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_category, container, false);

        listcategory = (RecyclerView) myFragment.findViewById(R.id.listCategory);
        listcategory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(container.getContext());
        listcategory.setLayoutManager(layoutManager);

        loadCategory();

        return myFragment;
    }

    private void loadCategory() {
        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                Category.class,
                R.layout.category_layout,
                CategoryViewHolder.class,
                categories

        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, final Category model, int position) {
                viewHolder.categoryName.setText(model.getName());
                Picasso.with(getActivity())
                        .load(model.getImage())
                        .into(viewHolder.categoryImg);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(getActivity(), String.format("%s|%s", adapter.getRef(position).getKey(), model.getName()), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        listcategory.setAdapter(adapter);
    }
}
