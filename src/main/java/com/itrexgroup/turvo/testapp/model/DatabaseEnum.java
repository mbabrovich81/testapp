package com.itrexgroup.turvo.testapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */

@Getter
@AllArgsConstructor
public enum DatabaseEnum {
    turvo("turvo.datasource", 100)
    , turvo2("turvo2.datasource", 100_000)
    , turvo3("turvo3.datasource", 10_000_000);

    private final String prefix;
    private final int rows;
}
