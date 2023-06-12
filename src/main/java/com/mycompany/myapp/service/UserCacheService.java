package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.AdminUserDTO;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserCacheService {

    private final Logger log = LoggerFactory.getLogger(UserCacheService.class);

    private final UserRepository userRepository;

    @Autowired
    private RedissonClient redissonClient;

    public UserCacheService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Async("taskExecutor")
    public void updateUserCache(Pageable pageable) {
        try {
            log.error("update cache async");
            RMap<String, Object> rMap = redissonClient.getMap("rocketMap");
            Thread.sleep(4000);
            long now = System.currentTimeMillis();
            // add 1 minutes
            rMap.put("expired", now + 60000 * 1);

            Page<AdminUserDTO> users = userRepository.findAll(pageable).map(AdminUserDTO::new);
            rMap.put("users", users);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
