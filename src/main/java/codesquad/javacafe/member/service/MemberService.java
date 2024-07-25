package codesquad.javacafe.member.service;

import codesquad.javacafe.member.dto.request.MemberCreateRequestDto;
import codesquad.javacafe.member.dto.request.MemberUpdateRequestDto;
import codesquad.javacafe.member.dto.response.MemberResponseDto;
import codesquad.javacafe.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

public class MemberService {
    private static final MemberService instance = new MemberService();
    private static final MemberRepository memberRepository = MemberRepository.getInstance();

    public static MemberService getInstance() {
        return instance;
    }

    public void createMember(MemberCreateRequestDto memberDto) {
        memberRepository.save(memberDto);
    }

    public List<MemberResponseDto> getMemberList() {
        var memberList = memberRepository.findAll();
        return memberList.stream()
                .map(member -> new MemberResponseDto(member))
                .collect(Collectors.toList());
    }

    public MemberResponseDto getMemberInfo(String userId) {
        return new MemberResponseDto(memberRepository.findByUserId(userId));
    }

    public void updateMember(MemberUpdateRequestDto memberDto) {
        memberRepository.update(memberDto);
    }
}
