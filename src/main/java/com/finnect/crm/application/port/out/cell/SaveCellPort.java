package com.finnect.crm.application.port.out.cell;

import com.finnect.crm.domain.cell.state.DataColumnState;
import com.finnect.crm.domain.cell.state.DataRowState;

public interface SaveCellPort {

    void saveNewCellByNewRow(DataColumnState column, DataRowState dataRow);

    void saveNewCellByNewColumn(DataColumnState column);
}