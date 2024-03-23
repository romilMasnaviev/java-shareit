package ru.practicum.shareit.item.dto;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentConverter {

    @Mapping(target = "authorName", ignore = true)
    CommentResponse convert(Comment comment);

    @AfterMapping
    default void setAuthorName(Comment comment, @MappingTarget CommentResponse response) {
        if (comment != null && comment.getAuthor() != null) {
            response.setAuthorName();
        }
    }

    List<CommentResponse> convert(List<Comment> comments);
}
