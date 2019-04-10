package com.itrexgroup.turvo.testapp.service.table;

import com.itrexgroup.turvo.testapp.dao.CreateReportDAO;
import com.itrexgroup.turvo.testapp.dao.MultiDatabaseDAO;
import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Table service.
 * Create and fill tables in DB
 * Created by maxim.babrovich on 02.04.2019.
 */

@Service
@Slf4j
public class CreateTableService implements ICreateTableService {

    private MultiDatabaseDAO multiDatabaseDAO;

    private CreateReportDAO createReportDAO;

    @Override
    @Transactional
    public void createAndFillTable(DatabaseEnum db) {

        try {
            log.info("[createAndFillTable] Start to preloading {} database", db.name().toUpperCase());
            log.info("[createAndFillTable] Creating (and dropping before if exists) tables. Db: {}", db.name().toUpperCase());
            // drop and create tbl_test_task with sequence
            multiDatabaseDAO.createTableWithDropping(db);

            // generate default values for table (field 'name')
            List<Object[]> names = new ArrayList<>();

            for (int i = 0; i < db.getRows(); i++) {
                names.add(new Object[]{"name-" + i});
            }

            // fill tbl_test_task by default values from list 'names'
            log.info("[createAndFillTable] Insert default values tables. Db: {}", db.name().toUpperCase());
            multiDatabaseDAO.insertData(db, names);
            log.info("[createAndFillTable] Default values inserted. Db: {}", db.name().toUpperCase());
        } catch (Exception e) {
            log.error("[ERROR][createAndFillTable] Create and fill table was failed. {} ", db.name().toUpperCase(), e);
        }
    }

    @Override
    @Transactional
    public void createReportTables() {

        try {
            log.info("[createReportTables] Creating (and dropping before if exists) report tables");
            // drop and create tbl_test_task with sequence
            createReportDAO.createTableWithDropping();
        } catch (Exception e) {
            log.error("[ERROR][createReportTables] Create report tables was failed.", e);
        }
    }

    @Autowired
    public void setMultiDatabaseDAO(MultiDatabaseDAO multiDatabaseDAO) {
        this.multiDatabaseDAO = multiDatabaseDAO;
    }

    @Autowired
    public void setCreateReportDAO(CreateReportDAO createReportDAO) {
        this.createReportDAO = createReportDAO;
    }
}