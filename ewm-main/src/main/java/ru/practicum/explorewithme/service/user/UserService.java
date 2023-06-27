package ru.practicum.explorewithme.service.user;

import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.model.user.NewUserRequest;
import ru.practicum.explorewithme.model.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsersByIds(List<Long> ids, Pageable pageable);

    List<UserDto> getUsersAll(Pageable pageable);

    UserDto createUser(NewUserRequest userRequest);

    void deleteUser(Long userId);
}
