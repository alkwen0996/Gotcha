package org.a602.gotcha.domain.participant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.a602.gotcha.domain.participant.exception.InvalidPhoneNumberException;
import org.a602.gotcha.domain.participant.request.*;
import org.a602.gotcha.domain.participant.response.ParticipantInfoResponse;
import org.a602.gotcha.domain.participant.response.ParticipantRankListResponse;
import org.a602.gotcha.domain.participant.service.ParticipantService;
import org.a602.gotcha.domain.problem.response.ProblemListResponse;
import org.a602.gotcha.domain.problem.service.ProblemService;
import org.a602.gotcha.global.common.BaseResponse;
import org.a602.gotcha.global.error.GlobalErrorCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Tag(name = "Participant", description = "게임 참여자 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/game")
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;
    private final ProblemService problemService;

    @Operation(description = "닉네임 중복체크 API", summary = "닉네임 중복체크 API")
    @ApiResponse(responseCode = "200", description = "닉네임 중복 검사 통과", content = @Content(schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "400", description = "중복된 닉네임 있음")
    @ApiResponse(responseCode = "404", description = "해당하는 방 찾을 수 없음")
    @PostMapping("/duplicate")
    public BaseResponse<Boolean> duplicateNickname(@Valid @RequestBody DuplicateNicknameRequest request) {
        Boolean isDuplicate = participantService.existDuplicateNickname(request);
        return new BaseResponse<>(isDuplicate);
    }

    @Operation(description = "참여자 신규로 등록하기 API", summary = "참여자 신규로 등록하기 API")
    @ApiResponse(responseCode = "200", description = "유저 등록 성공")
    @ApiResponse(responseCode = "400", description = "중복된 닉네임 있음")
    @ApiResponse(responseCode = "404", description = "해당하는 방 찾을 수 없음")
    @PostMapping("/register")
    public BaseResponse<Object> registerParticipant(@Valid @RequestBody ParticipantRegisterRequest request) {
        participantService.registerParticipant(request);
        return new BaseResponse<>(GlobalErrorCode.SUCCESS);
    }

    @Operation(description = "기존 참여자 방에 로그인하기 API", summary = "기존 참여자 방에 로그인하기 API")
    @ApiResponse(responseCode = "200", description = "참여자 정보 확인 성공", content = @Content(schema = @Schema(implementation = ParticipantInfoResponse.class)))
    @ApiResponse(responseCode = "401", description = "참여자 정보 일치하지 않음")
    @ApiResponse(responseCode = "404", description = "1. 해당하는 방 존재하지 않음 \t\n 2. 해당하는 유저 존재하지 않음")
    @PostMapping("/login")
    public BaseResponse<ParticipantInfoResponse> doLogin(@Valid @RequestBody ParticipantLoginRequest request) {
        ParticipantInfoResponse response = participantService.getParticipantInfo(request);
        return new BaseResponse<>(response);
    }

    @Operation(description = "게임 신규로 시작하기 API", summary = "게임 신규로 시작하기 API")
    @ApiResponse(responseCode = "200", description = "게임 신규 시작 성공", content = @Content(schema = @Schema(implementation = ProblemListResponse.class)))
    @ApiResponse(responseCode = "404", description = "1. 해당하는 방 없음 \t\n 2. 해당하는 유저 없음 \t\n 3. 해당하는 문제 없음 ")
    @PostMapping("/start")
    public BaseResponse<List<ProblemListResponse>> newGameStart(@Valid @RequestBody ParticipantGameStartRequest request) {
        // 유저 유효성 체크 및 시작 시간 추가
        participantService.updateStartTime(request);
        // 문제 탐색
        List<ProblemListResponse> problemList = problemService.getProblemList(request.getRoomId());
        return new BaseResponse<>(problemList);
    }

    @Operation(description = "게임 재참여하기 API", summary = "게임 재참여하기 API API")
    @ApiResponse(responseCode = "200", description = "게임 재참여 성공", content = @Content(schema = @Schema(implementation = ProblemListResponse.class)))
    @ApiResponse(responseCode = "404", description = "1. 해당하는 방 없음 \t\n 2. 해당하는 유저 없음 \t\n 3. 해당하는 문제 없음 ")
    @PostMapping("/rejoin")
    public BaseResponse<List<ProblemListResponse>> rejoinGame(@Valid @RequestBody RejoinGameRequest request) {
        // 유저 유효성 체크
        participantService.checkUserValidation(request.getRoomId(), request.getNickname());
        // 문제 탐색
        List<ProblemListResponse> problemList = problemService.getProblemList(request.getRoomId());
        return new BaseResponse<>(problemList);
    }

    @Operation(description = "최종 제출 기록 등록하기 API", summary = "최종 제출 기록 등록하기 API")
    @ApiResponse(responseCode = "200", description = "기록 등록 성공")
    @ApiResponse(responseCode = "404", description = "1. 해당하는 방 없음 \t\n 2. 해당하는 유저 없음")
    @PostMapping("/clear")
    public BaseResponse<List<ParticipantRankListResponse>> registerGameRecord(@Valid @RequestBody ProblemFinishRequest request) {
        participantService.updateGameRecord(request);
        return new BaseResponse<>(participantService.updateGameRecordToCache(request.getRoomId()));
    }

    @Operation(description = "휴대폰 번호 입력하기 API", summary = "휴대폰 번호 입력하기 API")
    @ApiResponse(responseCode = "200", description = "휴대폰 번호 등록 성공")
    @ApiResponse(responseCode = "400", description = "휴대폰 번호 형식이 올바르지 않음")
    @ApiResponse(responseCode = "404", description = "1. 해당하는 방 없음 \t\n 2. 해당하는 유저 없음")
    @PostMapping("/phonenumber")
    public BaseResponse<Object> registerPhoneNumber(@Valid @RequestBody RegisterPhonenumberRequest request) {
        if (!request.getPhoneNumber().matches("^01(\\d)-(\\d{3,4})-(\\d{4})$")) {
            throw new InvalidPhoneNumberException();
        }
        participantService.updatePhoneNumber(request);
        return new BaseResponse<>(GlobalErrorCode.SUCCESS);
    }

    @Operation(description = "랭킹 확인하기 API", summary = "랭킹 확인하기 API")
    @ApiResponse(responseCode = "200", description = "랭킹 가져오기 성공")
    @ApiResponse(responseCode = "404", description = "랭킹 받아오기 실패")
    @PostMapping("/rank")
    public BaseResponse<List<ParticipantRankListResponse>> getRankList(@Valid @RequestBody RankInfoRequest request) {
        List<ParticipantRankListResponse> rankList = participantService.getRankList(request);
        return new BaseResponse<>(rankList);
    }

}
