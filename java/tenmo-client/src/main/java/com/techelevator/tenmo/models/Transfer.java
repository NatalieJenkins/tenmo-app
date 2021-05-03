package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Transfer {

    private int transferId;
    private int transferTypeId;
    private int transferStatusId;
    private int transferFromAccountId;
    private int transferToAccountId;
    private BigDecimal transferAmount;

    public Transfer(int transferId, int transferTypeId, int transferStatusId, int transferFromAccountId,
                    int transferToAccountId, BigDecimal transferAmount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.transferFromAccountId = transferFromAccountId;
        this.transferToAccountId = transferToAccountId;
        this.transferAmount = transferAmount;
    }

    public Transfer(int transferToAccountId, BigDecimal transferAmount) {
        this.transferToAccountId = transferToAccountId;
        this.transferAmount = transferAmount;
    }

    public Transfer(int transferId) {
        this.transferId = transferId;
    }

    public Transfer() {

    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getTransferId() {

        return transferId;
    }

    public void setTransferId(int transferId) {

        this.transferId = transferId;
    }

    public int getTransferFromAccountId() {
        return transferFromAccountId;
    }

    public void setTransferFromAccountId(int transferFromAccountId) {
        this.transferFromAccountId = transferFromAccountId;
    }

    public int getTransferToAccountId() {
        return transferToAccountId;
    }

    public void setTransferToAccountId(int transferToAccountId) {
        this.transferToAccountId = transferToAccountId;
    }


    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {

        this.transferAmount = transferAmount;
    }
}
