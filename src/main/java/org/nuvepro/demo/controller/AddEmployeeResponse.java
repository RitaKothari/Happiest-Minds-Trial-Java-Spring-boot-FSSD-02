package org.nuvepro.demo.controller;

public class AddEmployeeResponse {
    private int employeeId;
    private String employeeName;
    private String responseMessage;

    private String status;

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int labId) {
        this.employeeId = labId;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
