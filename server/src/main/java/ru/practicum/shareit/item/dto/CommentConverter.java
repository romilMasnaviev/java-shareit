package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentConverter {

    @Mapping(target = "authorName", source = "author.name")
    CommentResponse convert(Comment comment);

    List<CommentResponse> convert(List<Comment> comments);
}