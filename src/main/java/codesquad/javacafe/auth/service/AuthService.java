package codesquad.javacafe.auth.service;

import java.util.Objects;

import codesquad.javacafe.auth.dto.request.LoginRequestDto;
import codesquad.javacafe.common.session.MemberInfo;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.member.entity.Member;
import codesquad.javacafe.member.repository.MemberRepository;

public class AuthService {
	private static final AuthService instance = new AuthService();

	private AuthService() {
	}

	public static AuthService getInstance() {
		return instance;
	}

	public MemberInfo getLoginInfo(LoginRequestDto requestDto) {
		Member member = MemberRepository.getInstance().findByUserId(requestDto.getUserId());

		if (Objects.isNull(member)) {
			throw ClientErrorCode.USER_NOT_FOUND.customException("User not found ,request info = "+requestDto);
		}
		// 비밀번호 불일치 시 예외 발생
		if (!Objects.equals(member.getPassword(), requestDto.getPassword())) {
			throw ClientErrorCode.INVALID_PASSWORD.customException("request member info = " + requestDto);
		}

		return new MemberInfo(member.getId(), member.getUserId(), member.getName());
	}
}
