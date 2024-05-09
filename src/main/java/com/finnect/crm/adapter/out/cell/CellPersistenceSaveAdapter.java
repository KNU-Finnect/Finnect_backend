package com.finnect.crm.adapter.out.cell;

import com.finnect.crm.adapter.out.cell.persistence.DataCellEntity;
import com.finnect.crm.adapter.out.column.DataColumnRepository;
import com.finnect.crm.adapter.out.column.persistence.DataColumnEntity;
import com.finnect.crm.adapter.out.cell.persistence.DataRowEntity;
import com.finnect.crm.application.port.out.column.SaveDataColumnPort;
import com.finnect.crm.application.port.out.cell.SaveDataRowPort;
import com.finnect.crm.application.port.out.cell.SaveCellPort;
import com.finnect.crm.domain.cell.DataColumn;
import com.finnect.crm.domain.cell.DataRow;
import com.finnect.crm.domain.cell.state.DataColumnState;
import com.finnect.crm.domain.cell.state.DataRowState;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Slf4j
public class CellPersistenceSaveAdapter implements SaveDataRowPort, SaveCellPort{
    private final DataCellRepository dataCellRepository;
    private final DataColumnRepository dataColumnRepository;
    private final DataRowRepository dataRowRepository;
    @Override
    @Transactional
    public DataRow saveNewDataRow() {
        DataRowEntity dataRowEntity = new DataRowEntity();
        dataRowRepository.save(dataRowEntity);
        log.info(dataRowEntity.getDataRowId().toString());

        return dataRowEntity.toDomain();
    }

    @Override
    public void saveNewCellByNewColumn(DataColumnState column) {
        log.info(column.getWorkspaceId().toString());

        List<DataRowEntity> dataRows = dataRowRepository.findDataRowEntitiesByWorkspaceId(column.getWorkspaceId());
        log.info(dataRows.toString());
        List<DataCellEntity> cellEntities = dataRows.stream()
                        .map(dataRowEntity -> DataCellEntity.builder()
                                .columnId(column.getColumnId())
                                .rowId(dataRowEntity.getDataRowId())
                                .build())
                        .toList();
        dataCellRepository.saveAll(cellEntities);
    }

    @Override
    public void saveNewCellByNewRow(DataColumnState column, DataRowState dataRow) {
        List<DataColumnEntity> columnEntities = dataColumnRepository.findDataColumnEntitiesByWorkspaceId(column.getWorkspaceId());
        log.info(columnEntities.toString());
        List<DataCellEntity> cellEntities = columnEntities.stream()
                .map(dataColumnEntity -> DataCellEntity.builder()
                        .columnId(dataColumnEntity.getColumnId())
                        .rowId(dataRow.getDataRowId())
                        .build())
                .toList();
        log.info("Saving New Cell");
        dataCellRepository.saveAll(cellEntities);
    }


}
