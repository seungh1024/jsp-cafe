package codesquad.javacafe.member.service;

import codesquad.javacafe.common.db.DBConnection;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.member.dto.request.MemberCreateRequestDto;
import codesquad.javacafe.member.dto.request.MemberUpdateRequestDto;
import codesquad.javacafe.member.dto.response.MemberResponseDto;
import codesquad.javacafe.member.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MemberService {
    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private static final MemberService instance = new MemberService();


    public static MemberService getInstance() {
        return instance;
    }

    public void createMember(MemberCreateRequestDto memberDto) {
        var member = memberDto.toEntity();
        MemberRepository.getInstance().save(member);

    }

    public List<MemberResponseDto> getMemberList() {
        var memberList = MemberRepository.getInstance().findAll();
        if (Objects.isNull(memberList)) {
            return null;
        }
        return memberList.stream()
                .map(member -> new MemberResponseDto(member))
                .collect(Collectors.toList());
    }

    public MemberResponseDto getMemberInfo(String userId) {
        return new MemberResponseDto(MemberRepository.getInstance().findByUserId(userId));
    }

    public MemberResponseDto getMemberById(long id) {
        return new MemberResponseDto(MemberRepository.getInstance().findById(id));
    }

    public void updateMember(MemberUpdateRequestDto memberDto) {
        int result = MemberRepository.getInstance().update(memberDto);
        if (result == 0) {
            throw ClientErrorCode.INVALID_PASSWORD.customException("update member info = "+memberDto);
        }
    }
}
