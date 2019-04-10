package com.itrexgroup.turvo.testapp.model.report;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class Report {

    private String reportUid;

    private String state;

    private String databaseName;

    private Long timeInNanos;

    private Timestamp startDate;

    private Timestamp endDate;

    private Timestamp createdDate;

    private String query;

    private String resMsg;

}
