package org.a602.gotcha.domain.reward.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.a602.gotcha.domain.reward.request.SetRewardRequest;
import org.a602.gotcha.domain.reward.service.RewardService;
import org.a602.gotcha.global.common.BaseResponse;
import org.a602.gotcha.global.error.GlobalErrorCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")

public class RewardController {
    private final RewardService rewardService;

    @PostMapping("/set/reward")
    @ApiResponse(description = "방에 리워드 설정", responseCode = "200")
    public BaseResponse<Void> setReward(@RequestBody SetRewardRequest request) {

        rewardService.setReward(request.getRewards(), request.getRoomId());
        return new BaseResponse<>(GlobalErrorCode.SUCCESS);
    }
}
