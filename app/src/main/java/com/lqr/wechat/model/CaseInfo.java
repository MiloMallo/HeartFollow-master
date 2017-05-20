package com.lqr.wechat.model;

/**
 * Created by Administrator on 2017-05-14.
 */

public class CaseInfo {
    public String imgRand;

    public String historyRecount;
    public String historyCurCase;
    public String historyPastCase;
    public String historySigns;

    public String assayRecount;
    public String imageRecount;
    public String medicationRecount;

    public CaseInfo() {
    }

    public CaseInfo(String imgRand,String historyRecount,String historyCurCase,String historyPastCase,String historySigns,String assayRecount,String imageRecount,String medicationRecount) {
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
