package com.finnect.crm.application.port.in.column;

import com.finnect.crm.domain.cell.DataColumn;
import com.finnect.crm.domain.cell.state.DataColumnState;
import java.util.List;

public interface LoadDataColumnUseCase {

    List<DataColumnState> loadDataColumns(DataColumn dataColumn);
}