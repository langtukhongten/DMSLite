package CommonLib;

/**
 * Created by My PC on 17/02/2016.
 */
public class OrderDeliveryItem {
    public String orderNo;
    public int orderPrice;
    public byte paymentMethod; // 0=cod, 1=prepaid
    public long createdDate;
    public byte status; // 0=unknown, 1=assigned, 2=completed, 3=canceled, 4=other
    public String nameAddress;
    public String phoneNumber;
    public String description;
}
