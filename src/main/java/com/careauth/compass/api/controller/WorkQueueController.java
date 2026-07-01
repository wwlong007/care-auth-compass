package com.careauth.compass.api.controller;

import com.careauth.compass.application.workqueue.WorkQueueItem;
import com.careauth.compass.application.workqueue.WorkQueueService;
import com.careauth.compass.domain.model.QueueName;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/work-queues")
public class WorkQueueController {
    private final WorkQueueService workQueueService;

    public WorkQueueController(WorkQueueService workQueueService) {
        this.workQueueService = workQueueService;
    }

    @GetMapping("/{queue}/items")
    public List<WorkQueueItem> items(@PathVariable("queue") QueueName queueName) {
        return workQueueService.findItems(queueName);
    }
}
