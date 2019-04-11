package com.itrexgroup.turvo.testapp.model.report;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class Report {

    // report uid
    private String reportUid;

    // report state
    private ReportState state;

    //duration time in nanos
    private Long timeInNanos;

    // database
    private DatabaseEnum databaseName;

    //start date
    private Timestamp startDate;

    //end date
    private Timestamp endDate;

    //created date
    private Timestamp createdDate;

    // query
    private String query;

    // result message
    private String resMsg;
}
