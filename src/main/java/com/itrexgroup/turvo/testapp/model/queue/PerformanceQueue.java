package com.itrexgroup.turvo.testapp.model.queue;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Builder
public class PerformanceQueue {

    // report uid
    private String reportUid;

    //number of attempts
    private int attemptsNum;

    //created date
    private Date createdDate;

    // query
    private String query;

}
