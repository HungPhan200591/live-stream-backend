package com.stream.demo.repository;

import com.stream.demo.model.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Stream entity
 */
@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {

    /**
     * Tìm stream theo stream key
     */
    Optional<Stream> findByStreamKey(String streamKey);

    /**
     * Tìm tất cả stream của một creator
     */
    List<Stream> findByCreatorId(Long creatorId);

    /**
     * Tìm tất cả stream đang live
     */
    List<Stream> findByIsLiveTrue();

    /**
     * Kiểm tra stream key đã tồn tại chưa
     */
    boolean existsByStreamKey(String streamKey);

    /**
     * Tìm stream theo ID và creator ID (cho việc check ownership)
     */
    Optional<Stream> findByIdAndCreatorId(Long id, Long creatorId);
}
