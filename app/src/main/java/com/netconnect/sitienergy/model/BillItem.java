package com.netconnect.sitienergy.model;


import java.io.Serializable;

/**
 * Created by Vimal Kumar
 */

public class BillItem implements Serializable {
    private String aggName;
    private String billDate;
    private String paidAmt;
    private String remainAmt;
    private String advAmt;
    private String payMode;

    public BillItem(String aggName, String billDate, String paidAmt, String remainAmt, String advAmt, String payMode) {
        super();
        this.aggName = aggName;
        this.billDate = billDate;
        this.paidAmt = paidAmt;
        this.remainAmt = remainAmt;
        this.advAmt = advAmt;
        this.payMode = payMode;
    }

    public String getAdvAmt() {
        return advAmt;
    }

    public String getAggName() {
        return aggName;
    }

    public String getBillDate() {
        return billDate;
    }

    public String getPaidAmt() {
        return paidAmt;
    }

    public String getRemainAmt() {
        return remainAmt;
    }

    public String getPayMode() {
        return payMode;
    }

}

