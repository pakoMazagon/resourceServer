package com.tpv.resource_server.controller;

import com.tpv.resource_server.dto.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
public class ResourceController {
	@GetMapping("/user")
	public ResponseEntity<MessageDTO> user(Authentication authentication) {
		return ResponseEntity.ok(new MessageDTO("Hello " + authentication.getName()));
	}

	@GetMapping("/admin")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<MessageDTO> admin(Authentication authentication) {
		return ResponseEntity.ok(new MessageDTO("Hello ADMIN. " + authentication.getName()));
	}
}
