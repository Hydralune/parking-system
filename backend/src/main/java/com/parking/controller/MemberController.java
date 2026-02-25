package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.dto.ResponseResult;
import com.parking.entity.Member;
import com.parking.entity.User;
import com.parking.mapper.UserMapper;
import com.parking.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserMapper userMapper;

    private Long getUserId(Principal principal) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", principal.getName()));
        if (user == null) throw new RuntimeException("用户不存在");
        return user.getId();
    }

    @GetMapping
    public ResponseResult<?> getMembers(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Member> page = new Page<>(current, size);
        Page<Member> result = memberService.page(page);
        // 填充 username
        result.getRecords().forEach(m -> {
            User u = userMapper.selectById(m.getUserId());
            if (u != null) m.setUsername(u.getUsername());
        });
        return ResponseResult.success(result);
    }

    @GetMapping("/me")
    public ResponseResult<Member> getMyMember(Principal principal) {
        Long userId = getUserId(principal);
        return ResponseResult.success(memberService.getMemberByUserId(userId));
    }

    @GetMapping("/discount")
    public ResponseResult<Double> getDiscount(Principal principal) {
        Long userId = getUserId(principal);
        return ResponseResult.success(memberService.getDiscountRate(userId));
    }

    @GetMapping("/points")
    public ResponseResult<Integer> getPoints(Principal principal) {
        Long userId = getUserId(principal);
        return ResponseResult.success(memberService.getPoints(userId));
    }

    @PostMapping
    public ResponseResult<Member> createMember(@RequestBody Member member, Principal principal) {
        Long userId = getUserId(principal);
        member.setUserId(userId);
        return ResponseResult.success("开通会员成功", memberService.createMember(member));
    }

    // 管理员为指定用户创建会员
    @PostMapping("/admin")
    public ResponseResult<Member> createMemberByAdmin(@RequestBody Member member) {
        if (member.getUserId() == null) return ResponseResult.error("请指定用户ID");
        return ResponseResult.success("开通会员成功", memberService.createMember(member));
    }

    // 按用户名搜索用户（供管理员选择）
    @GetMapping("/search-user")
    public ResponseResult<?> searchUser(@RequestParam String keyword) {
        List<User> users = userMapper.selectList(
            new QueryWrapper<User>()
                .like("username", keyword)
                .or().like("phone", keyword)
                .last("LIMIT 10")
        );
        return ResponseResult.success(users.stream().map(u -> {
            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("phone", u.getPhone());
            return m;
        }).collect(java.util.stream.Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseResult<Member> updateMember(@PathVariable Long id, @RequestBody Member member) {
        member.setId(id);
        return ResponseResult.success(memberService.updateMember(member));
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> deleteMember(@PathVariable Long id) {
        return ResponseResult.success(memberService.deleteMember(id));
    }
}
