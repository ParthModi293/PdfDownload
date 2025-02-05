package com.email.pdfdownload.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequestDto {

    private String fromDate;
    private String toDate;
    private String clientName;
    private List<AllScript> scripts;
    private String ledgerBalance;

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public List<AllScript> getScripts() {
        return scripts;
    }

    public void setScripts(List<AllScript> scripts) {
        this.scripts = scripts;
    }

    public String getLedgerBalance() {
        return ledgerBalance;
    }

    public void setLedgerBalance(String ledgerBalance) {
        this.ledgerBalance = ledgerBalance;
    }
}
