package com.lqr.wechat.model;

import java.sql.Date;

/**
 * Created by Administrator on 2017-05-14.
 */

public class CaseRecount {
    public int _id;
    public String userAccount;
    public String date;
    public String imgRand;

    public String historyRecount;
    public String historyCurCase;
    public String historyPastCase;
    public String historySigns;

    public String assayRecount;
    public String imageRecount;
    public String medicationRecount;

    public CaseRecount() {
    }

    public CaseRecount(String userAccount, String date, String imgRand, String historyRecount, String historyCurCase, String historyPastCase, String historySigns, String assayRecount, String imageRecount, String medicationRecount) {
        this.userAccount = userAccount;
        this.date = date;
        this.imgRand = imgRand;
        this.historyRecount = historyRecount;
        this.historyCurCase = historyCurCase;
        this.historyPastCase = historyPastCase;
        this.historySigns = historySigns;
        this.assayRecount = assayRecount;
        this.imageRecount = imageRecount;
        this.medicationRecount = medicationRecount;
    }
}
