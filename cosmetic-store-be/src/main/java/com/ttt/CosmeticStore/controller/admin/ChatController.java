package com.ttt.CosmeticStore.controller.admin;

import com.ttt.CosmeticStore.dto.response.ChatRoomResponse;
import com.ttt.CosmeticStore.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//
//    @Controller
//    @RequestMapping("/admin")
//    public class ChatController {
//
//        @GetMapping("/chat")
//        public String chatPage() {
//            return "Chat";
//        }
//    }
//
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/chat")
    public String chatPage(Model model) {
        // Load active chat rooms for initial display
        List<ChatRoomResponse> chatRooms = chatService.getAllActiveChatRooms();
        model.addAttribute("chatRooms", chatRooms);
        return "Chat";
    }
}