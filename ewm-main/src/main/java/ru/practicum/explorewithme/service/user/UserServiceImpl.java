package ru.practicum.explorewithme.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.model.exception.BadRequestException;
import ru.practicum.explorewithme.model.user.NewUserRequest;
import ru.practicum.explorewithme.model.user.UserDto;
import ru.practicum.explorewithme.model.user.UserMapper;
import ru.practicum.explorewithme.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids, Pageable pageable) {
        return userRepository.getUsersByIdIn(ids, pageable).stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getUsersAll(Pageable pageable) {
        return userRepository.findAll(pageable).stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest userRequest) {
        if (250 < userRequest.getName().length() || userRequest.getName().length() < 2) {
            throw new BadRequestException("Недопустимая длинна имени пользователя ," + userRequest.getName().length());
        }
        if (254 < userRequest.getEmail().length() || userRequest.getEmail().length() < 6) {
            throw new BadRequestException("Недопустимая длинна имени пользователя ," + userRequest.getEmail().length());
        }
        return mapper.toUserDto(userRepository.save(mapper.toUser(userRequest)));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
