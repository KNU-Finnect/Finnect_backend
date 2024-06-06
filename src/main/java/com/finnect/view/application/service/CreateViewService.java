package com.finnect.view.application.service;

import com.finnect.view.application.port.in.CreateViewUseCase;
import com.finnect.crm.domain.column.DataType;
import com.finnect.view.application.port.out.SaveViewPort;
import com.finnect.view.domain.View;
import com.finnect.view.domain.state.ViewState;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateViewService implements CreateViewUseCase {
    private final SaveViewPort saveViewPort;
    @Override
    public ViewState createNewView(View view) {
        log.info(view.toString());
        return saveViewPort.saveNewView(view);
    }

    @Override
    @Transactional
    public void createDefaultView(Long workspaceId) {
        List<View> defaultView = new ArrayList<>();

        log.info(String.valueOf(workspaceId));

        defaultView.add(
                View.builder()
                        .workspaceId(workspaceId)
                        .viewName("Company")
                        .isMain(true)
                        .dType(DataType.COMPANY)
                .build());

        defaultView.add(
                View.builder()
                        .workspaceId(workspaceId)
                        .viewName("Deal")
                        .isMain(true)
                        .dType(DataType.DEAL)
                .build()
        );

        log.info("default View Generated");
        log.info(defaultView.toString());
        saveViewPort.saveDefaultViews(new ArrayList<>(defaultView));
    }
}
