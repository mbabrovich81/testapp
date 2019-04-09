package com.itrexgroup.turvo.testapp.controller.dto;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import com.itrexgroup.turvo.testapp.model.report.ReportState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * Created by maxim.babrovich on 05.04.2019.
 */

@Data
@AllArgsConstructor
public class ReportItemDTO {

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

    // result message
    private String resMsg;
}
