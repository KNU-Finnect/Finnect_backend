package com.finnect.crm.adapter.in.web.controller.person;

import com.finnect.common.ApiUtils;
import com.finnect.crm.adapter.in.web.req.person.CreatePersonRequest;
import com.finnect.crm.adapter.in.web.req.person.UpdatePersonRequest;
import com.finnect.crm.adapter.in.web.res.person.CreatePersonResponse;
import com.finnect.crm.adapter.in.web.res.person.FindPeopleResponse;
import com.finnect.crm.adapter.in.web.res.person.PersonDto;
import com.finnect.crm.adapter.in.web.res.person.UpdatePersonResponse;
import com.finnect.crm.application.port.in.person.*;
import com.finnect.crm.domain.person.PersonState;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PersonController {
    private final CreatePersonUsecase createPersonUsecase;
    private final UpdatePersonUsecase updatePersonUsecase;
    private final FindPeopleUsecase findPeopleUsecase;

    @PostMapping("/workspaces/people")
    public ResponseEntity<ApiUtils.ApiResult<CreatePersonResponse>> createPerson(@RequestBody CreatePersonRequest request) {
        PersonState state = createPersonUsecase.createPerson(
                new CreatePersonCommand(
                        request.getCompanyId(),
                        request.getPersonName(),
                        request.getPersonRole(),
                        request.getPersonEmail(),
                        request.getPersonPhone()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiUtils.success(HttpStatus.CREATED, CreatePersonResponse.of(PersonDto.from(state)))
        );
    }

    @PatchMapping("/workspaces/people")
    public ResponseEntity<ApiUtils.ApiResult<UpdatePersonResponse>> updatePerson(@RequestBody UpdatePersonRequest request) {
        PersonState state = updatePersonUsecase.update(
                new UpdatePersonCommand(
                        request.getPersonId(),
                        request.getPersonName(),
                        request.getPersonRole(),
                        request.getPersonEmail(),
                        request.getPersonPhone()
                )
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiUtils.success(HttpStatus.OK, UpdatePersonResponse.of(PersonDto.from(state)))
        );
    }

    @GetMapping("/workspaces/people")
    public ResponseEntity<ApiUtils.ApiResult<FindPeopleResponse>> findPeople(@RequestParam("companyId") Long companyId) {
        List<PersonState> people = findPeopleUsecase.findPeople(companyId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiUtils.success(HttpStatus.OK, FindPeopleResponse.of(
                        people.stream().map(PersonDto::from).collect(Collectors.toList()))
                ));
    }
}
