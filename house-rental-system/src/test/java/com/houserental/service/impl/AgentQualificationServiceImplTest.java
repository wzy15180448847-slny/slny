package com.houserental.service.impl;

import com.houserental.entity.AgentQualification;
import com.houserental.entity.User;
import com.houserental.mapper.AgentQualificationMapper;
import com.houserental.mapper.UserMapper;
import com.houserental.common.result.PageResult;
import com.houserental.service.AgentQualificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgentQualificationServiceImplTest {

    @Mock
    private AgentQualificationMapper agentQualificationMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AgentQualificationServiceImpl agentQualificationService;

    @Test
    public void testSubmitQualification() {
        // 准备测试数据
        User agent = new User();
        agent.setId(1L);
        agent.setUserType("AGENT");
        agent.setStatus(0);

        AgentQualification qualification = new AgentQualification();
        qualification.setAgentId(1L);
        qualification.setCompanyName("Test Company");
        qualification.setBusinessLicense("1234567890");
        qualification.setLicenseImage("license.jpg");
        qualification.setQualificationCert("Q123456");
        qualification.setCertImage("cert.jpg");
        qualification.setContactName("Test Contact");
        qualification.setContactPhone("13800138000");

        // 模拟方法调用
        when(userMapper.selectById(1L)).thenReturn(agent);
        when(agentQualificationMapper.selectByAgentId(1L)).thenReturn(null);
        when(agentQualificationMapper.insert(any(AgentQualification.class))).thenReturn(1);

        // 执行测试
        boolean result = agentQualificationService.submitQualification(qualification);

        // 验证结果
        assertTrue(result);
        assertEquals(0, qualification.getAuditStatus());
        assertNotNull(qualification.getCreateTime());
        assertNotNull(qualification.getUpdateTime());
    }

    @Test
    public void testAuditQualification() {
        // 准备测试数据
        User agent = new User();
        agent.setId(1L);
        agent.setUserType("AGENT");
        agent.setStatus(0);

        AgentQualification qualification = new AgentQualification();
        qualification.setId(1L);
        qualification.setAgentId(1L);
        qualification.setAuditStatus(0);

        // 模拟方法调用
        when(agentQualificationMapper.selectById(1L)).thenReturn(qualification);
        when(agentQualificationMapper.updateById(any(AgentQualification.class))).thenReturn(1);
        when(userMapper.selectById(1L)).thenReturn(agent);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        boolean result = agentQualificationService.auditQualification(1L, 1, "审核通过", 2L);

        // 验证结果
        assertTrue(result);
        assertEquals(1, qualification.getAuditStatus());
        assertEquals("审核通过", qualification.getAuditRemark());
        assertEquals(2L, qualification.getAuditorId());
        assertNotNull(qualification.getAuditTime());
        assertNotNull(qualification.getUpdateTime());
    }

    @Test
    public void testGetByAgentId() {
        // 准备测试数据
        AgentQualification qualification = new AgentQualification();
        qualification.setId(1L);
        qualification.setAgentId(1L);

        // 模拟方法调用
        when(agentQualificationMapper.selectByAgentId(1L)).thenReturn(qualification);

        // 执行测试
        AgentQualification result = agentQualificationService.getByAgentId(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getAgentId());
    }
}
