package com.finnect.view.application.port.out;

import com.finnect.crm.domain.column.state.DataColumnState;
import com.finnect.view.domain.View;
import com.finnect.view.domain.state.ViewColumnState;
import com.finnect.view.domain.state.ViewState;
import java.util.List;

public interface LoadViewPort {

    View loadView(ViewState viewState);

    List<View> loadViewsByColumn(List<DataColumnState> columns);
}
