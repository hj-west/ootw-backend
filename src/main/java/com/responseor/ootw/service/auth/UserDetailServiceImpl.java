package com.responseor.ootw.service.auth;

import com.responseor.ootw.dto.auth.CustomUserDetails;
import com.responseor.ootw.entity.Member;
import com.responseor.ootw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUuid(Long.valueOf(username)).orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 회원입니다."));

        return new CustomUserDetails(member);
    }
}
