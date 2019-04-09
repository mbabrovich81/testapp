package com.itrexgroup.turvo.testapp.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by maxim.babrovich on 05.04.2019.
 */

@Data
@AllArgsConstructor
public class QueryPerformanceDTO {

    // query
    private String query;

    // num of report
    private String reportUid;

    // link to get report
    private String reportLink;
}
