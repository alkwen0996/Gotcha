package org.a602.gotcha.domain.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberLoginResponse {
	private final Long id;
	private final String nickname;
	private final String organization;
	private final String email;
	private final String registrationId;
	private final String accessToken;
	private final String refreshToken;

	@Builder
	public MemberLoginResponse(final Long id, final String nickname, final String organization, final String email,
		final String registrationId,
		final String accessToken, final String refreshToken) {
		this.id = id;
		this.nickname = nickname;
		this.organization = organization;
		this.email = email;
		this.registrationId = registrationId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

}
