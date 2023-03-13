package org.a602.gotcha.domain.participant.service;

import lombok.RequiredArgsConstructor;
import org.a602.gotcha.domain.participant.entity.Participant;
import org.a602.gotcha.domain.participant.exception.DuplicateNicknameException;
import org.a602.gotcha.domain.participant.exception.ParticipantNotFoundException;
import org.a602.gotcha.domain.participant.repository.ParticipantQueryRepository;
import org.a602.gotcha.domain.participant.repository.ParticipantRepository;
import org.a602.gotcha.domain.participant.request.ParticipantCheckRequest;
import org.a602.gotcha.domain.participant.request.ParticipantGameStartRequest;
import org.a602.gotcha.domain.participant.response.ParticipantInfoResponse;
import org.a602.gotcha.domain.room.entity.Room;
import org.a602.gotcha.domain.participant.exception.ParticipantLoginFailedException;
import org.a602.gotcha.domain.room.exception.RoomNotFoundException;
import org.a602.gotcha.domain.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ParticipantService {

    private final ParticipantQueryRepository participantQueryRepository;
    private final ParticipantRepository participantRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public Participant registerUser(ParticipantCheckRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(RoomNotFoundException::new);
        List<Participant> participants = participantQueryRepository.searchByRoomAndNickname(room, request.getNickname());
        if (participants.size() == 0) {
            return participantRepository.save(
                    Participant.builder()
                            .nickname(request.getNickname())
                            .password(request.getPassword())
                            .room(room)
                            .isFinished(false)
                            .build()
            );
        } else {
            throw new DuplicateNicknameException();
        }
    }

    @Transactional(readOnly = true)
    public ParticipantInfoResponse getUserInfo(ParticipantCheckRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(RoomNotFoundException::new);
        List<Participant> participants = participantQueryRepository.searchByRoomAndNickname(room, request.getNickname());
        if (participants.size() == 0) {
            throw new ParticipantNotFoundException();
        } else {
            Participant user = participants.get(0);
            if (user.getPassword().equals(request.getPassword())) {
                return ParticipantInfoResponse.builder()
                        .isFinished(user.getIsFinished())
                        .startTime(user.getStartTime())
                        .build();
            } else throw new ParticipantLoginFailedException();
        }
    }

    @Transactional(readOnly = true)
    public void updateStartTime(ParticipantGameStartRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(RoomNotFoundException::new);
        List<Participant> participants = participantQueryRepository.searchByRoomAndNickname(room, request.getNickname());
        if (participants.size() == 0) {
            throw new ParticipantNotFoundException();
        } else {
            Participant user = participants.get(0);
            user.updateStartTime(request.getStartTime());
        }
    }

    @Transactional(readOnly = true)
    public boolean checkUserValidation(Long roomId, String nickname) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(RoomNotFoundException::new);
        List<Participant> participants = participantQueryRepository.searchByRoomAndNickname(room, nickname);
        return !participants.isEmpty();
    }
}
