package com.gea.shoppinglist;

public class ProductModel {
    private String Id;
    private String Name, Amount, Checked;

    public ProductModel(String id, String name, String amount, String checked) {
        Id = id;
        Name = name;
        Amount = amount;
        Checked = checked;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getChecked() {
        return Checked;
    }

    public void setChecked(String checked) {
        Checked = checked;
    }
}
