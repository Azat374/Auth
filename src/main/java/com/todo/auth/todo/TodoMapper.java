package com.todo.auth.todo;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TodoMapper {
    TodoRequestPayload toDto(Todo entity);
    Todo toTodo(TodoRequestPayload dto);
}
