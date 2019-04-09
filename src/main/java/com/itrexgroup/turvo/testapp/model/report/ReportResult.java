package com.itrexgroup.turvo.testapp.model.report;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by maxim.babrovich on 03.04.2019.
 */
@Data
public class ReportResult implements Serializable {

    // report uid
    private String reportUid;

    // report state
    private ReportState state;

    //duration time in nanos
    private Long requestTime;

    // database
    private DatabaseEnum database;

    //start date
    private Timestamp requestStart;

    //end date
    private Timestamp requestEnd;

    //created date
    private Timestamp createdDate;

    // query
    private String query;

    // result message
    private String resMsg;
}
