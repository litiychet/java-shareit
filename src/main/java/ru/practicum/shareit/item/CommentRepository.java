package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(Long itemId);

    @Query("select c " +
            "from Comment c " +
            "where c.item.id in :item")
    List<Comment> findByItemsIdIn(List<Long> item);
}
