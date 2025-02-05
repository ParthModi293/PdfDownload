package com.email.pdfdownload.dto;

import lombok.Data;

@Data
public class AllScript {

    private String scriptName;
    private String buyQty;
    private String buyAvg;
    private String sellAvg;
    private String sellQty;
    private String posQty;
    private String ltp;
    private String m2m;

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getBuyQty() {
        return buyQty;
    }

    public void setBuyQty(String buyQty) {
        this.buyQty = buyQty;
    }

    public String getBuyAvg() {
        return buyAvg;
    }

    public void setBuyAvg(String buyAvg) {
        this.buyAvg = buyAvg;
    }

    public String getSellAvg() {
        return sellAvg;
    }

    public void setSellAvg(String sellAvg) {
        this.sellAvg = sellAvg;
    }

    public String getSellQty() {
        return sellQty;
    }

    public void setSellQty(String sellQty) {
        this.sellQty = sellQty;
    }

    public String getPosQty() {
        return posQty;
    }

    public void setPosQty(String posQty) {
        this.posQty = posQty;
    }

    public String getLtp() {
        return ltp;
    }

    public void setLtp(String ltp) {
        this.ltp = ltp;
    }

    public String getM2m() {
        return m2m;
    }

    public void setM2m(String m2m) {
        this.m2m = m2m;
    }
}
