package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.TaskDtos.CreateTaskRequest;
import com.legal.casemanagement.dto.TaskDtos.TaskListItem;
import com.legal.casemanagement.dto.TaskDtos.TaskResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private TaskService taskService;

    @Test
    @SuppressWarnings("unchecked")
    void getTasks_noFilters_returnsEmptyList() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<TaskListItem> result = taskService.getTasks(null, null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createTask_nullRequest_returnsNull() {
        TaskResponse result = taskService.createTask(null);
        assertNull(result);
    }

    @Test
    void createTask_blankTitle_returnsNull() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("  ");
        TaskResponse result = taskService.createTask(request);
        assertNull(result);
    }

    @Test
    void updateTaskStatus_withValidId_returnsTrue() {
        // Use anyString() + anyObject matchers to match the actual varargs form
        when(jdbcTemplate.update(anyString(), anyString(), any(Long.class))).thenReturn(1);
        boolean result = taskService.updateTaskStatus(1L, "COMPLETED");
        assertTrue(result);
    }

    @Test
    void updateTaskStatus_withInvalidId_returnsFalse() {
        when(jdbcTemplate.update(anyString(), anyString(), any(Long.class))).thenReturn(0);
        boolean result = taskService.updateTaskStatus(9999L, "COMPLETED");
        assertFalse(result);
    }
}
