package com.finnect.view.domain;

import com.finnect.view.domain.constant.SortCondition;
import com.finnect.view.domain.state.ViewColumnState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@AllArgsConstructor
@Builder
@ToString
public class ViewColumn implements ViewColumnState {
    private Long columnId;
    private Long viewId;
    private Double index;
    private Boolean hided;
    private SortCondition sorting;

    @Override
    public Long getColumnId() {
        return this.columnId;
    }

    @Override
    public Long getViewId() {
        return this.viewId;
    }

    @Override
    public Double getIndex() {
        return this.index;
    }

    @Override
    public SortCondition getSort() {
        return this.sorting;
    }

    @Override
    public Boolean isHided() {
        return this.hided;
    }

    public void updateColumnInfo(ViewColumn viewColumn){
        this.index = viewColumn.getIndex();
        this.hided = viewColumn.isHided();
        this.sorting = viewColumn.getSort();
    }
}
