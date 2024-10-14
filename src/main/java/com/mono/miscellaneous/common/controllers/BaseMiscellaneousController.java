package com.mono.miscellaneous.common.controllers;

import com.mono.miscellaneous.common.logger.FileLogger;
import com.mono.miscellaneous.common.utilities.CommonEnum;
import com.mono.miscellaneous.payload.ErrorObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseMiscellaneousController {
    @Autowired
    public FileLogger fileLogger;
    private HttpServletRequest httpServletRequest;

    private SimpleDateFormat ft3 = new SimpleDateFormat( "yyDDDHHmm"); //"ddmmss");
    private SimpleDateFormat ft4 = new SimpleDateFormat("yyyyMMddHHmmss");

    private static int sequenceNumber;

    public void setRequest(HttpServletRequest request) {
        this.httpServletRequest = request;
    }

    protected String getServiceID() {
        Date myDate = new Date();
        String sequenceString = "0000" + String.valueOf(sequenceNumber);
        if(sequenceNumber < 1000)
            sequenceNumber++;
        else
            sequenceNumber = 0;
        String serviceID = ft3.format(myDate).toString() + sequenceString.substring(sequenceString.length() - 3, sequenceString.length()); //+ (new Random().nextInt(10)); //+ myRandom;
        return serviceID;

    }

    protected String getTimeStamp() {
        Date myDate = new Date();
        return ft4.format(myDate).toString();
    }

    private String getClientIP() {
        String remoteAddr = "";

        if (httpServletRequest != null) {
            remoteAddr = httpServletRequest.getRemoteAddr();
        }

        return remoteAddr;
    }

    private String getOperationName() {
        String operationName = "";

        try {
            operationName = httpServletRequest.getRequestURI() + "/" + httpServletRequest.getMethod();
        } catch (Exception ex) {
            operationName = "";
        }

        return operationName;
    }


    protected void logRequest(Object request) {
//        setClientOperationDetails();
        fileLogger.requestLogger(getClientIP(), getOperationName(), request, getServiceID(), false);
    }

    protected void logResponse(Object response) {
        fileLogger.responseLogger(getClientIP(), getOperationName(), response, getServiceID(), false);
    }

    protected ResponseEntity handleException(Exception ex) {
        ex.printStackTrace();
        ResponseEntity resp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorObject(getServiceID(), CommonEnum.ResponseCode.CONN_ERROR.getCode(),ex.getMessage(),getTimeStamp()));
        fileLogger.responseLogger(getClientIP(), getOperationName(), resp, getServiceID(), true);
        return resp;
    }
}
