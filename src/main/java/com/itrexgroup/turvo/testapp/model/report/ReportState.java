package com.itrexgroup.turvo.testapp.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by maxim.babrovich on 05.04.2019.
 */

@Getter
@AllArgsConstructor
public enum ReportState {
    success(0)
    , in_waiting_queue(1)
    , in_progress(2)
    , failed(3)
    , not_exists(4);

    private final int rate;
}
