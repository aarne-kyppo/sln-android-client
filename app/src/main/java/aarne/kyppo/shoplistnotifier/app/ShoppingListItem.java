package aarne.kyppo.shoplistnotifier.app;

import java.io.Serializable;

/**
 * Created by aarnek on 12/11/14.
 */
public class ShoppingListItem implements Serializable {
    int id=-1;
    int list_id;
    String name;
    int qty = 1;
    boolean checked = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getList_id() {
        return list_id;
    }

    public void setList_id(int list_id) {
        this.list_id = list_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
