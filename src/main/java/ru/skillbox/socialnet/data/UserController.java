package ru.skillbox.socialnet.data;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.skillbox.socialnet.data.dto.UserDto;


@Controller
public class UserController {

    @GetMapping("/api/v1/users/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse<UserDto>> GetUserById (@PathVariable(value = "id") Integer id,
                                                             @RequestHeader("authorization") String token) {
        ApiResponse<UserDto> response = new ApiResponse<>();
        return ResponseEntity.ok(response);
    }
}
