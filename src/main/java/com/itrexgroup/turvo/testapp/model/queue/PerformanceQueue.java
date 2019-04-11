package com.itrexgroup.turvo.testapp.model.queue;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class PerformanceQueue {

    // report uid
    private String reportUid;

    //number of attempts
    private int attemptsNum;

    //created date
    private Timestamp createdDate;

    // query
    private String query;

}
