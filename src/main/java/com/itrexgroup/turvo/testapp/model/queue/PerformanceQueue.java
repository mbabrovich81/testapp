package com.itrexgroup.turvo.testapp.model.queue;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class PerformanceQueue {

    private String reportUid;

    private int attemptsNum;

    private Timestamp createdDate;

    private String query;

}
