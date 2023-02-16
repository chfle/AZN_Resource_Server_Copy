package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Messages;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface MessagesRepository extends CrudRepository<Messages, Long> {
    /**
     * Only one message should exist
     */
    @Query(value = "select * FROM messages where user_id = ?1 and  message_type_data->'year'=?2 and message_type_data->'month'=?3 and read = false", nativeQuery = true)
    Optional<Messages> findMessagesByUserIdAndYearAndMonth(Long userid, String year, String month);

    @Transactional
    @Modifying
    @Query(value = "update messages set read = ?1 where message_id = ?2 and user_id = ?3", nativeQuery = true)
    void setRead(boolean value, Long messageId, Long userId);

}
