package com.gea.shoppinglist;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private RecyclerView list;
    private ArrayList<ProductModel> productModelArrayList;
    private ProductAdapter productAdapter;
    private Database database;
    private Context context;
    private FloatingActionButton addBtn;
    private TextView statusTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        list = findViewById(R.id.shoppingList);
        addBtn = findViewById(R.id.addBtn);
        statusTxt = findViewById(R.id.statusTxt);
        database = new Database(context);
        loadList();
        list.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
               showAlertDailog();
            }
        });
        listStatus();

        //swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final ProductModel deleteItem = ((ProductAdapter) Objects.requireNonNull(list.getAdapter())).getItem(viewHolder.getAdapterPosition());
                final int deleteIndex = viewHolder.getAdapterPosition();
                productAdapter.removeItem(viewHolder.getAdapterPosition());
//                productModelArrayList.remove(viewHolder.getAdapterPosition());
                database.deleteListItem(deleteItem.getId());
                // undo
                final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Item Deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                        new Database(context).addToList(deleteItem);
                        productAdapter.restoreItem(deleteIndex, deleteItem);
                        productAdapter.notifyDataSetChanged();
                        listStatus();
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
                listStatus();
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(list);


    }
    private void loadList(){
        productModelArrayList = database.getList();
        productAdapter = new ProductAdapter(context, productModelArrayList);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setAdapter(productAdapter);
    }

    @Override
    protected void onResume() {
        loadList();
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showAlertDailog(){
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        int width = metrics.widthPixels;
//        int height = metrics.heightPixels;
//        customDialogClass.getWindow().setLayout((6 * width)/7, LinearLayout.LayoutParams.WRAP_CONTENT);


        LayoutInflater factory = LayoutInflater.from(context);
        final View alertDialogView = factory.inflate(R.layout.custom_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // transparent background
        alertDialog.setView(alertDialogView);
        final TextView nameTxt = alertDialogView.findViewById(R.id.nameTxt);
        final TextView amountTxt = alertDialogView.findViewById(R.id.amountTxt);

        alertDialogView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uniqueID = UUID.randomUUID().toString();
                database.addToList(new ProductModel(uniqueID, nameTxt.getText().toString(), amountTxt.getText().toString(), "flase"));
                nameTxt.setText("");
                amountTxt.setText("");
                alertDialog.dismiss();
                loadList();
                listStatus();
            }
        });
        alertDialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showAlertDailog2(){

        LayoutInflater factory = LayoutInflater.from(context);
        final View alertDialogView2 = factory.inflate(R.layout.custom_dialog2, null);
        final AlertDialog alertDialog2 = new AlertDialog.Builder(this).create();
        Objects.requireNonNull(alertDialog2.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // transparent background
        alertDialog2.setView(alertDialogView2);

        alertDialogView2.findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.cleanList();
                Toast.makeText(context,"Cleared", Toast.LENGTH_SHORT).show();
                loadList();
                listStatus();
                alertDialog2.dismiss();
            }
        });
        alertDialogView2.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog2.dismiss();
            }
        });
        alertDialog2.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.search);
        SearchView mSearchView = (SearchView) searchViewMenuItem.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                productAdapter.getFilter().filter(s);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clearList) {
            showAlertDailog2();
            return true;
        }
        return false;
    }
    public void listStatus(){
        if(productAdapter.getItemCount() > 0){
            statusTxt.setVisibility(View.GONE);
        }else{
            statusTxt.setVisibility(View.VISIBLE);
        }
    }
}


