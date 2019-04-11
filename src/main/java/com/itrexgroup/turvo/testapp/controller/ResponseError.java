package com.itrexgroup.turvo.testapp.controller;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by maxim.babrovich on 11.04.2019.
 */

@Getter
@Builder
public class ResponseError {

    private int errorCode;

    private String errorStatus;

    private String errorMsg;

    private Date timestamp;

}
