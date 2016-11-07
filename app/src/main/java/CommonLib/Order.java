package CommonLib;

import java.util.ArrayList;

/**
 * Created by Admin on 5/24/2016.
 */
public class Order {
    public int rowId;
    public String no_;
    public String name;
    public long time;
    public int id_customer;
    public int status;//0 - moi 1 - da duyet 2 - mo lai 4 - tu choi 9 - huy
    public String note;
    public float amount;
    public int id_company;
    public int id_region;
    public int id_branch;
    public int id_employee;
    public int id_parent;
    public int document_type; //0: Bán hàng trực tiếp 1: BC Tồn kho cửa hiệu 2: Đề nghị nhập tồn 3: Bán Hàng trên tồn kho nhân viên 4: kho công ty
    public String imageUrl;
    public String employeeName;
    public long ref_id;
    public ArrayList<OrderDetail> orderDetails;
}
