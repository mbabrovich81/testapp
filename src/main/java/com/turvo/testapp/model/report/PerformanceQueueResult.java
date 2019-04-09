package com.turvo.testapp.model.report;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by maxim.babrovich on 05.04.2019.
 */
@Data
public class PerformanceQueueResult implements Serializable {

    // report uid
    private String reportUid;

    //number of attempts
    private int attemptsNum;

    //created date
    private Timestamp createdDate;

    // query
    private String query;
}
