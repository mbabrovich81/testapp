package com.itrexgroup.turvo.testapp.model.report;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

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
    private Date startDate;

    //end date
    private Date endDate;

    //created date
    private Date createdDate;

    // query
    private String query;

    // result message
    private String resMsg;
}
