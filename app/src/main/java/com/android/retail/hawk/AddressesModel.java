package com.android.retail.hawk;

public class AddressesModel {

    private String fullName;
    private String address;
    private String pinCode;
    private Boolean selected;

    public AddressesModel(String fullName, String address, String pinCode, Boolean selected) {
        this.fullName = fullName;
        this.address = address;
        this.pinCode = pinCode;
        this.selected = selected;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
